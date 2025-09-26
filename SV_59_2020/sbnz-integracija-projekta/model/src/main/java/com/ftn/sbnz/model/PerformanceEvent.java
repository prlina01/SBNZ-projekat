package com.ftn.sbnz.model;

import java.util.Date;

public class PerformanceEvent {

    private Server server;
    private double averageRating;
    private Date timestamp;

    public PerformanceEvent(Server server, double averageRating) {
        this.server = server;
        this.averageRating = averageRating;
        this.timestamp = new Date();
    }

    // Getters and Setters

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
