package com.ftn.sbnz.model;

public class Rating {

    private Long id;
    private User user;
    private Server server;
    private int score; // 1-5

    public Rating() {
    }

    public Rating(Long id, User user, Server server, int score) {
        this.id = id;
        this.user = user;
        this.server = server;
        this.score = score;
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
}
