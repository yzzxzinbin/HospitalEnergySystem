package com.hospital.energymgmt.dto;

import com.hospital.energymgmt.model.User; // Added import
import com.hospital.energymgmt.model.Role; // Added import
import java.util.stream.Collectors; // Added import for roles

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role; // Assuming role is a simple string in DTO for now
    private String password; // Added password field

    public UserDto() {
    }

    public UserDto(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Constructor to convert User entity to UserDto
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            // Assuming Role class has a getName() method or similar to get the role identifier
            this.role = user.getRoles().stream()
                            .map(Role::getName) // Or role -> role.getName()
                            .collect(Collectors.joining(","));
        } else {
            this.role = ""; // Or null, or some default
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}