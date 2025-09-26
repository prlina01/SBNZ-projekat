package com.ftn.sbnz.service.api;

import com.ftn.sbnz.model.auth.Role;
import com.ftn.sbnz.service.api.dto.AuthResponse;
import com.ftn.sbnz.service.api.dto.LoginRequest;
import com.ftn.sbnz.service.api.dto.RegisterRequest;
import com.ftn.sbnz.model.User;
import com.ftn.sbnz.service.auth.UserService;
import com.ftn.sbnz.service.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(user.getUsername(), user.getRoles());
        Set<String> roles = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        return ResponseEntity.ok(new AuthResponse(token, roles));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String username = authentication.getName();
    User account = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        String token = jwtService.generateToken(username, account.getRoles());
        Set<String> roles = account.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        return ResponseEntity.ok(new AuthResponse(token, roles));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
