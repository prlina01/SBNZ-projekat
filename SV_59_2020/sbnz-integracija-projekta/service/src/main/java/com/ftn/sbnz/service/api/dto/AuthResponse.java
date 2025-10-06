package com.ftn.sbnz.service.api.dto;

import com.ftn.sbnz.model.User;

import java.util.Set;

public class AuthResponse {
    private String token;
    private Set<String> roles;
    private User.UserStatus status;

    public AuthResponse() {
    }

    public AuthResponse(String token, Set<String> roles, User.UserStatus status) {
        this.token = token;
        this.roles = roles;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public User.UserStatus getStatus() {
        return status;
    }

    public void setStatus(User.UserStatus status) {
        this.status = status;
    }
}
