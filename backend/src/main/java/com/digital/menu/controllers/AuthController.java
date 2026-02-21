package com.digital.menu.controllers;

import com.digital.menu.dto.AuthRequest;
import com.digital.menu.dto.AuthResponse;
import com.digital.menu.dto.RegisterAdminRequest;
import com.digital.menu.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AuthResponse> registerAdmin(
        @RequestBody RegisterAdminRequest request,
        @RequestHeader(value = "X-Setup-Key", required = false) String setupKey
    ) {
        return ResponseEntity.ok(authService.register(request, setupKey));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
