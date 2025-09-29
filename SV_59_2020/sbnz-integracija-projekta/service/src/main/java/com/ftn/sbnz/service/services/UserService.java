package com.ftn.sbnz.service.services;

import com.ftn.sbnz.model.User;
import com.ftn.sbnz.model.auth.Role;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_USER_USERNAME = "demo";
    private static final String DEFAULT_USER_PASSWORD = "user123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void ensureDefaultUsersExist() {
        createUserIfMissing(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, Set.of(Role.ADMIN));
        createUserIfMissing(DEFAULT_USER_USERNAME, DEFAULT_USER_PASSWORD, Set.of(Role.USER));
    }

    public User registerUser(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), Set.of(Role.USER));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private void createUserIfMissing(String username, String rawPassword, Set<Role> roles) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), roles);
        userRepository.save(user);
    }
}
