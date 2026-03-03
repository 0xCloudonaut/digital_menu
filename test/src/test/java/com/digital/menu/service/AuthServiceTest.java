package com.digital.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.digital.menu.dto.AuthRequest;
import com.digital.menu.dto.AuthResponse;
import com.digital.menu.dto.RegisterAdminRequest;
import com.digital.menu.model.AdminUser;
import com.digital.menu.repository.AdminUserRepository;
import com.digital.menu.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(adminUserRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        ReflectionTestUtils.setField(authService, "allowPublicRegistration", true);

        RegisterAdminRequest request = new RegisterAdminRequest();
        request.setTenantId("tenant-a");
        request.setUsername("admin");
        request.setPassword("password123");

        when(adminUserRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(jwtService.generateToken(any())).thenReturn("token-123");

        AuthResponse response = authService.register(request, null);

        assertEquals("token-123", response.getToken());
        assertEquals("tenant-a", response.getTenantId());
        assertEquals("admin", response.getUsername());

        ArgumentCaptor<AdminUser> savedUser = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).save(savedUser.capture());
        assertEquals("ROLE_ADMIN", savedUser.getValue().getRole());
        assertEquals("encoded", savedUser.getValue().getPasswordHash());
    }

    @Test
    void register_shouldRejectInvalidSetupKeyWhenPublicRegistrationDisabled() {
        ReflectionTestUtils.setField(authService, "allowPublicRegistration", false);
        ReflectionTestUtils.setField(authService, "setupKey", "expected-key");

        RegisterAdminRequest request = new RegisterAdminRequest();
        request.setTenantId("tenant-a");
        request.setUsername("admin");
        request.setPassword("password123");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(request, "wrong-key")
        );

        assertEquals("Invalid setup key", ex.getMessage());
    }

    @Test
    void register_shouldRejectInvalidRole() {
        ReflectionTestUtils.setField(authService, "allowPublicRegistration", true);

        RegisterAdminRequest request = new RegisterAdminRequest();
        request.setTenantId("tenant-a");
        request.setUsername("admin");
        request.setPassword("password123");
        request.setRole("guest");

        when(adminUserRepository.existsByUsername("admin")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(request, null)
        );

        assertEquals("Invalid role", ex.getMessage());
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("password123");

        AdminUser user = new AdminUser();
        user.setUsername("admin");
        user.setTenantId("tenant-a");
        user.setRole("ROLE_ADMIN");
        user.setPasswordHash("encoded");

        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("token-456");

        AuthResponse response = authService.login(request);

        assertEquals("token-456", response.getToken());
        assertEquals("tenant-a", response.getTenantId());
        assertEquals("admin", response.getUsername());

        verify(authenticationManager).authenticate(any());
    }
}
