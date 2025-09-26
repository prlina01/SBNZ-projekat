package com.ftn.sbnz.model;

public class PerformanceReport {

    private Server server;
    private String message;

    public PerformanceReport(Server server, String message) {
        this.server = server;
        this.message = message;
    }

    // Getters and Setters

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
