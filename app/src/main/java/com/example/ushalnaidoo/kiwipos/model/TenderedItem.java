package com.example.ushalnaidoo.kiwipos.model;

import com.example.ushalnaidoo.kiwipos.enums.ItemType;
import com.example.ushalnaidoo.kiwipos.enums.SaleStatus;

public class TenderedItem {
    private String product;
    private Integer quantity;
    private ItemType type;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
