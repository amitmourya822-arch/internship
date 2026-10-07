package com.handholding.controller;

import com.handholding.dto.AuthRequest;
import com.handholding.entity.AuthUser;
import com.handholding.repository.AuthUserRepository;
import com.handholding.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody AuthUser user) {

        try {

            if (repository.findByUsername(user.getUsername()) != null) {
                return "Username already exists";
            }

            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );

            repository.save(user);

            return "User Registered Successfully";

        } catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {

        try {

            AuthUser user =
                    repository.findByUsername(request.getUsername());

            if (user == null) {
                return "User not found";
            }

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {

                return "Invalid password";
            }

            return JwtUtil.generateToken(user.getUsername());

        } catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();
        }
    }
}