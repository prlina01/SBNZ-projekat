package com.ftn.sbnz.model;

public class VelocityModifier {
    private Server server;
    private double modifier;

    public VelocityModifier(Server server, double modifier) {
        this.server = server;
        this.modifier = modifier;
    }

    // Getters and Setters
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }
}
