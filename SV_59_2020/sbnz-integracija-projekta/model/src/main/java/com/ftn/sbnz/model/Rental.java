package com.ftn.sbnz.model;

import java.util.Date;

public class Rental {

    private Long id;
    private User user;
    private Server server;
    private Date startDate;
    private Date endDate;
    private String purpose;

    public Rental() {
    }

    public Rental(Long id, User user, Server server, Date startDate, Date endDate, String purpose) {
        this.id = id;
        this.user = user;
        this.server = server;
        this.startDate = startDate;
        this.endDate = endDate;
        this.purpose = purpose;
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
}
