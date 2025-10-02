package com.ftn.sbnz.model;

import com.ftn.sbnz.model.catalog.ServiceOffering;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_offering_id", nullable = false)
    private ServiceOffering serviceOffering;

    @Transient
    private Server server;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(nullable = false)
    private String purpose;

    @Column(name = "duration_days", nullable = false)
    private int durationDays = 0;

    @Column(name = "rating_score")
    private Integer ratingScore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "rated_at")
    private Date ratedAt;

    public Rental() {
    }

    public Rental(Long id, User user, Server server, Date startDate, Date endDate, String purpose) {
        this(id, user, server, startDate, endDate, purpose, deriveDurationDays(startDate, endDate));
    }

    public Rental(Long id, User user, Server server, Date startDate, Date endDate, String purpose, int durationDays) {
        this.id = id;
        this.user = user;
        this.server = server;
        this.startDate = startDate;
        this.endDate = endDate;
        this.purpose = purpose;
        this.durationDays = durationDays;
    }

    private static int deriveDurationDays(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        long days = ChronoUnit.DAYS.between(startInstant.atZone(ZoneOffset.UTC).toLocalDate(),
                endInstant.atZone(ZoneOffset.UTC).toLocalDate());
        return (int) Math.max(days, 0);
    }

    @PostLoad
    public void hydrateServer() {
        if (this.serviceOffering != null) {
            this.server = this.serviceOffering.toServer();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceOffering getServiceOffering() {
        return serviceOffering;
    }

    public void setServiceOffering(ServiceOffering serviceOffering) {
        this.serviceOffering = serviceOffering;
        if (serviceOffering != null) {
            this.server = serviceOffering.toServer();
        }
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = Math.max(durationDays, 0);
    }

    public Integer getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }

    public Date getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(Date ratedAt) {
        this.ratedAt = ratedAt;
    }

    public Date getPlannedEndDate() {
        if (startDate == null || durationDays <= 0) {
            return endDate;
        }
        Instant instant = startDate.toInstant().plus(Duration.ofDays(durationDays));
        return Date.from(instant);
    }
}
