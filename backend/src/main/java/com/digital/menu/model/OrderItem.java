package com.digital.menu.model;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private String dishId;
    private String dishName;
    private int quantity;
    private double unitPrice;
    private List<String> selectedAddOns = new ArrayList<>();
    private String itemStatus = "PLACED";
    private String kitchenStation = "MAIN";

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<String> getSelectedAddOns() {
        return selectedAddOns;
    }

    public void setSelectedAddOns(List<String> selectedAddOns) {
        this.selectedAddOns = selectedAddOns;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getKitchenStation() {
        return kitchenStation;
    }

    public void setKitchenStation(String kitchenStation) {
        this.kitchenStation = kitchenStation;
    }
}
