package com.ftn.sbnz.service.api.dto;

import com.ftn.sbnz.model.User;

import java.util.Set;

public class UserProfileResponse {

    private String username;
    private User.UserStatus status;
    private Set<String> roles;

    public UserProfileResponse() {
    }

    public UserProfileResponse(String username, User.UserStatus status, Set<String> roles) {
        this.username = username;
        this.status = status;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User.UserStatus getStatus() {
        return status;
    }

    public void setStatus(User.UserStatus status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
