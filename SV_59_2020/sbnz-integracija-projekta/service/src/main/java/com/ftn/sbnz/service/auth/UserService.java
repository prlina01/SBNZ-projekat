package com.ftn.sbnz.service.auth;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void ensureAdminUserExists() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), Set.of(Role.ADMIN));
            userRepository.save(admin);
        }
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
}
