package com.digital.menu.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthResponseTest {

    @Test
    void shouldReturnConstructorValues() {
        AuthResponse response = new AuthResponse("jwt-token", "tenant-1", "owner");

        assertEquals("jwt-token", response.getToken());
        assertEquals("tenant-1", response.getTenantId());
        assertEquals("owner", response.getUsername());
    }
}
