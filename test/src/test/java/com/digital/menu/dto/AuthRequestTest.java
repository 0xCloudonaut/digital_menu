package com.digital.menu.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthRequestTest {

    @Test
    void shouldSetAndGetUsernameAndPassword() {
        AuthRequest request = new AuthRequest();

        request.setUsername("admin-user");
        request.setPassword("secret-pass");

        assertEquals("admin-user", request.getUsername());
        assertEquals("secret-pass", request.getPassword());
    }
}
