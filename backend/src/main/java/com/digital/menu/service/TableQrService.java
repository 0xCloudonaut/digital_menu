package com.digital.menu.service;

import com.digital.menu.dto.QrTokenResponse;
import com.digital.menu.model.TableQrCode;
import com.digital.menu.repository.TableQrCodeRepository;
import com.digital.menu.security.QrTokenService;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TableQrService {
    private final TableQrCodeRepository tableQrCodeRepository;
    private final QrTokenService qrTokenService;

    @Value("${app.public-menu-base-url:http://localhost:3000}")
    private String publicMenuBaseUrl;

    @Value("${app.qr.expiration-seconds:2592000}")
    private long expirationSeconds;

    public TableQrService(TableQrCodeRepository tableQrCodeRepository, QrTokenService qrTokenService) {
        this.tableQrCodeRepository = tableQrCodeRepository;
        this.qrTokenService = qrTokenService;
    }

    public QrTokenResponse getOrCreateForTable(String tenantId, Integer tableNumber) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new IllegalArgumentException("tableNumber must be greater than 0");
        }

        TableQrCode existing = tableQrCodeRepository.findByTenantIdAndTableNumberAndActiveTrue(tenantId, tableNumber)
            .orElse(null);
        if (existing != null && !isExpired(existing)) {
            return toResponse(existing);
        }

        if (existing != null) {
            existing.setActive(false);
            existing.setUpdatedAt(Instant.now());
            tableQrCodeRepository.save(existing);
        }

        return generateNew(tenantId, tableNumber);
    }

    public QrTokenResponse regenerateForTable(String tenantId, Integer tableNumber) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new IllegalArgumentException("tableNumber must be greater than 0");
        }

        tableQrCodeRepository.findByTenantIdAndTableNumberAndActiveTrue(tenantId, tableNumber).ifPresent(existing -> {
            existing.setActive(false);
            existing.setUpdatedAt(Instant.now());
            tableQrCodeRepository.save(existing);
        });

        return generateNew(tenantId, tableNumber);
    }

    public void revokeForTable(String tenantId, Integer tableNumber) {
        TableQrCode existing = tableQrCodeRepository.findByTenantIdAndTableNumberAndActiveTrue(tenantId, tableNumber)
            .orElseThrow(() -> new IllegalArgumentException("Active QR not found for this table"));
        existing.setActive(false);
        existing.setUpdatedAt(Instant.now());
        tableQrCodeRepository.save(existing);
    }

    public List<QrTokenResponse> listForTenant(String tenantId) {
        return tableQrCodeRepository.findByTenantIdOrderByTableNumberAsc(tenantId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public QrTokenService.QrContext validatePublicQrToken(String token) {
        QrTokenService.QrContext context = qrTokenService.parseToken(token);
        TableQrCode qrCode = tableQrCodeRepository
            .findByTenantIdAndTableNumberAndTokenAndActiveTrue(context.tenantId(), context.tableNumber(), token)
            .orElseThrow(() -> new IllegalArgumentException("QR token is invalid or revoked"));

        if (isExpired(qrCode)) {
            qrCode.setActive(false);
            qrCode.setUpdatedAt(Instant.now());
            tableQrCodeRepository.save(qrCode);
            throw new IllegalArgumentException("QR token expired");
        }

        return context;
    }

    private QrTokenResponse generateNew(String tenantId, Integer tableNumber) {
        Instant now = Instant.now();
        String token = qrTokenService.generateToken(tenantId, tableNumber);
        TableQrCode qrCode = new TableQrCode();
        qrCode.setTenantId(tenantId);
        qrCode.setTableNumber(tableNumber);
        qrCode.setToken(token);
        qrCode.setMenuUrl(publicMenuBaseUrl + "/?t=" + token);
        qrCode.setActive(true);
        qrCode.setCreatedAt(now);
        qrCode.setUpdatedAt(now);
        qrCode.setExpiresAt(now.plusSeconds(expirationSeconds));
        return toResponse(tableQrCodeRepository.save(qrCode));
    }

    private boolean isExpired(TableQrCode qrCode) {
        return qrCode.getExpiresAt() != null && qrCode.getExpiresAt().isBefore(Instant.now());
    }

    private QrTokenResponse toResponse(TableQrCode qrCode) {
        return new QrTokenResponse(
            qrCode.getToken(),
            qrCode.getMenuUrl(),
            qrCode.getTenantId(),
            qrCode.getTableNumber(),
            qrCode.isActive(),
            qrCode.getCreatedAt(),
            qrCode.getUpdatedAt(),
            qrCode.getExpiresAt()
        );
    }
}
