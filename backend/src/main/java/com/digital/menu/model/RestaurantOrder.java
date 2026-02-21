package com.digital.menu.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
@CompoundIndexes({
    @CompoundIndex(
        name = "tenant_table_idempotency_uq",
        def = "{'tenantId': 1, 'tableNumber': 1, 'idempotencyKey': 1}",
        unique = true,
        sparse = true
    )
})
public class RestaurantOrder {
    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private Integer tableNumber;

    private List<OrderItem> items = new ArrayList<>();
    private String status = "PLACED";
    private String tableState = "ORDERING";
    private String source = "QR";
    private String idempotencyKey;
    private Instant expectedReadyAt;
    private boolean delayed;
    private List<OrderEvent> auditTrail = new ArrayList<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTableState() {
        return tableState;
    }

    public void setTableState(String tableState) {
        this.tableState = tableState;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getExpectedReadyAt() {
        return expectedReadyAt;
    }

    public void setExpectedReadyAt(Instant expectedReadyAt) {
        this.expectedReadyAt = expectedReadyAt;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public List<OrderEvent> getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(List<OrderEvent> auditTrail) {
        this.auditTrail = auditTrail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
