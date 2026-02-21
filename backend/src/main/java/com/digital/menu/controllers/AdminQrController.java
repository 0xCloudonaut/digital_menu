package com.digital.menu.controllers;

import com.digital.menu.dto.QrTokenResponse;
import com.digital.menu.security.TenantContext;
import com.digital.menu.service.TableQrService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/qr")
public class AdminQrController {
    private final TableQrService tableQrService;

    public AdminQrController(TableQrService tableQrService) {
        this.tableQrService = tableQrService;
    }

    @GetMapping("/tables")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public List<QrTokenResponse> listTableQrs() {
        String tenantId = TenantContext.getTenantIdOrThrow();
        return tableQrService.listForTenant(tenantId);
    }

    @GetMapping("/tables/{tableNumber}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public QrTokenResponse createTableQr(@PathVariable Integer tableNumber) {
        String tenantId = TenantContext.getTenantIdOrThrow();
        return tableQrService.getOrCreateForTable(tenantId, tableNumber);
    }

    @PostMapping("/tables/{tableNumber}/regenerate")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public QrTokenResponse regenerateTableQr(@PathVariable Integer tableNumber) {
        String tenantId = TenantContext.getTenantIdOrThrow();
        return tableQrService.regenerateForTable(tenantId, tableNumber);
    }

    @DeleteMapping("/tables/{tableNumber}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public void revokeTableQr(@PathVariable Integer tableNumber) {
        String tenantId = TenantContext.getTenantIdOrThrow();
        tableQrService.revokeForTable(tenantId, tableNumber);
    }
}
