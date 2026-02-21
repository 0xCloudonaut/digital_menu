package com.digital.menu.dto;

import com.digital.menu.model.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class PublicPlaceOrderRequest {
    private String qrToken;
    private String idempotencyKey;
    private List<OrderItem> items = new ArrayList<>();

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
