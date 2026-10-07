package com.handholding.controller;

import com.handholding.dto.AuthRequest;
import com.handholding.dto.LoginResponse;
import com.handholding.dto.RegisterRequest;
import com.handholding.entity.AuthUser;
import com.handholding.repository.AuthUserRepository;
import com.handholding.security.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthUserRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (repository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
        }

        AuthUser user = new AuthUser();
        user.setUsername(request.getUsername());

        // Self-registration is always a mentee; roles are provisioned server-side.
        user.setRole("MENTEE");
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        repository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {

        AuthUser user = repository.findByUsername(request.getUsername());

        if (user == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));
        }

        String token = JwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        user.getUsername(),
                        user.getRole()
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}