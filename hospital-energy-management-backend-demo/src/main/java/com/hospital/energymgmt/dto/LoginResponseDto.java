package com.hospital.energymgmt.dto;

public class LoginResponseDto {
    private String token;
    private String username;
    // You can add other user-related information if needed, e.g., roles

    public LoginResponseDto(String token, String username) {
        this.token = token;
        this.username = username;
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
}
