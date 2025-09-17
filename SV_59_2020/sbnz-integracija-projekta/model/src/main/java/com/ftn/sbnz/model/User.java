package com.ftn.sbnz.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    public enum UserStatus {
        NONE, BRONZE, SILVER, GOLD
    }

    private Long id;
    private String username;
    private UserStatus status;
    private List<Rental> rentals = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    public User() {
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
        this.status = UserStatus.NONE;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
