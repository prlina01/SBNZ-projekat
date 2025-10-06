package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.auth.Role;
import com.ftn.sbnz.service.api.dto.UserProfileResponse;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> currentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        UserProfileResponse response = new UserProfileResponse(user.getUsername(), user.getStatus(), roles);
        return ResponseEntity.ok(response);
    }
}
