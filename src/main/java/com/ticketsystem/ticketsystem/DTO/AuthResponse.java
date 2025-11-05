package com.ticketsystem.ticketsystem.DTO;

public class AuthResponse {
    private String username;
    private String token;
    private String role;
    
    public String getUsername() {
        return username;
    }
    public String getToken() {
        return token;
    }
    public String getRole() {
        return role;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setRole(String role) {
        this.role = role;
    }    
}
