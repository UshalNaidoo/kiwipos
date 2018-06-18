package com.example.ushalnaidoo.kiwipos.model;

import com.example.ushalnaidoo.kiwipos.enums.SaleStatus;

import java.util.List;

public class TenderedSale extends Sale {

    private List<TenderedItem> items;

    public TenderedSale(String id, String time, String notes, Double amount, Boolean takeAway, SaleStatus status) {
        super(id, time, notes, amount, takeAway, status);
    }

    public List<TenderedItem> getItems() {
        return items;
    }

    public void setItems(List<TenderedItem> items) {
        this.items = items;
    }
}

