package com.ftn.sbnz.model;

import java.time.LocalDate;

public class Rental {

    private Long id;
    private User user;
    private Server server;
    private LocalDate startDate;
    private LocalDate endDate;

    public Rental() {
    }

    public Rental(Long id, User user, Server server, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.user = user;
        this.server = server;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
