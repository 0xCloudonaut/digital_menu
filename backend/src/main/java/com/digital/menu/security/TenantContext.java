package com.digital.menu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TenantContext {
    private TenantContext() {}

    public static String getTenantIdOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AdminUserPrincipal principal)) {
            throw new IllegalStateException("Tenant context missing");
        }
        return principal.getTenantId();
    }
}
