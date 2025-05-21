package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.LoginRequestDto;
import com.hospital.energymgmt.dto.LoginResponseDto;
import com.hospital.energymgmt.dto.RegistrationRequestDto;
import com.hospital.energymgmt.model.User;
import com.hospital.energymgmt.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            logger.info("Login attempt for user: {}", loginRequestDto.getUsername());
            LoginResponseDto loginResponse = authService.login(loginRequestDto);
            logger.info("Login successful for user: {}", loginRequestDto.getUsername());
            return ResponseEntity.ok(loginResponse);
        } catch (UsernameNotFoundException e) {
            logger.warn("Login failed for user {}: {}", loginRequestDto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            logger.error("Login failed for user {}: {}", loginRequestDto.getUsername(), e.getMessage(), e);
            if ("USER_DISABLED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled.");
            } else if ("INVALID_CREDENTIALS".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: An unexpected error occurred. Please check server logs.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto registrationRequestDto) { // 修改参数类型为 RegistrationRequestDto
        User userToRegister = new User();
        userToRegister.setUsername(registrationRequestDto.getUsername());
        userToRegister.setPassword(registrationRequestDto.getPassword());
        userToRegister.setEmail(registrationRequestDto.getEmail());

        try {
            User registeredUser = authService.register(userToRegister);
            // Avoid returning the full user object with password. Consider a UserResponseDto.
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Please login."); // 返回更简洁的消息
        } catch (Exception e) {
            // Log the exception for server-side analysis
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
}