package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.LoginRequest;
import com.hospital.energymgmt.dto.RegistrationRequest; // Import RegistrationRequest
import com.hospital.energymgmt.model.User;
import com.hospital.energymgmt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = authService.login(loginRequest);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) { // Use RegistrationRequest
        User userToRegister = new User();
        userToRegister.setUsername(registrationRequest.getUsername());
        userToRegister.setPassword(registrationRequest.getPassword());
        userToRegister.setEmail(registrationRequest.getEmail()); // Set email from RegistrationRequest

        try {
            User registeredUser = authService.register(userToRegister);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
}