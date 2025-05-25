package com.hospital.energymgmt.dto;

public class LoginResponseDto {
    private String token;
    private String username;
    private Long id; // Added user ID
    // You can add other user-related information if needed, e.g., roles

    public LoginResponseDto(String token, String username, Long id) { // Updated constructor
        this.token = token;
        this.username = username;
        this.id = id; // Set user ID
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() { // Getter for ID
        return id;
    }

    public void setId(Long id) { // Setter for ID
        this.id = id;
    }
}
