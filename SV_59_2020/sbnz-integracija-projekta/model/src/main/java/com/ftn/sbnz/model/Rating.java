package com.ftn.sbnz.model;

import java.util.Date;

public class Rating {

    private Long id;
    private User user;
    private Server server;
    private int score; // 1-5
    private Date timestamp;
    private Rental rental;

    public Rating() {
    }

    public Rating(Long id, User user, Server server, int score, Date timestamp, Rental rental) {
        this.id = id;
        this.user = user;
        this.server = server;
        this.score = score;
        this.timestamp = timestamp;
        this.rental = rental;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }
}
