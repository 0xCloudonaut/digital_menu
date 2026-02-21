package com.digital.menu.dto;

public class AuthResponse {
    private String token;
    private String tenantId;
    private String username;

    public AuthResponse(String token, String tenantId, String username) {
        this.token = token;
        this.tenantId = tenantId;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUsername() {
        return username;
    }
}
