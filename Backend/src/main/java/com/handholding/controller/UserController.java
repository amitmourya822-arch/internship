package com.handholding.controller;

import com.handholding.dto.UserRequest;
import com.handholding.dto.UserResponse;
import com.handholding.entity.User;
import com.handholding.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("ADMIN", "MENTOR", "MENTEE");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET ALL USERS
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(toResponse(user));
    }

    // CREATE USER
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest request) {

        String role = normalizeRole(request.getRole());
        if (role == null) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Invalid role"));
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("message", "Email already exists"));
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Password is required"));
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "User not found"));
        }

        String role = normalizeRole(request.getRole());
        if (role == null) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Invalid role"));
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        User saved = userRepository.save(user);

        return ResponseEntity.ok(toResponse(saved));
    }

    // DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "User not found"));
        }

        userRepository.deleteById(id);

        return ResponseEntity.ok(
                java.util.Map.of("message", "User deleted successfully")
        );
    }

    private String normalizeRole(String role) {

        if (role == null || role.isBlank()) {
            return "MENTEE";
        }

        String upper = role.toUpperCase();

        return ALLOWED_ROLES.contains(upper) ? upper : null;
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}