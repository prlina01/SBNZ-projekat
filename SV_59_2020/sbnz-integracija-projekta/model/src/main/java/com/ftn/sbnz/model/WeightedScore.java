package com.ftn.sbnz.model;

public class WeightedScore {
    private Server server;
    private double score;
    private String purpose;

    public WeightedScore(Server server, double score, String purpose) {
        this.server = server;
        this.score = score;
        this.purpose = purpose;
    }

    // Getters and Setters
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
