package com.digital.menu.dto;

public class TableStateSummary {
    private Integer tableNumber;
    private String state;
    private int openOrders;

    public TableStateSummary(Integer tableNumber, String state, int openOrders) {
        this.tableNumber = tableNumber;
        this.state = state;
        this.openOrders = openOrders;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public String getState() {
        return state;
    }

    public int getOpenOrders() {
        return openOrders;
    }
}
