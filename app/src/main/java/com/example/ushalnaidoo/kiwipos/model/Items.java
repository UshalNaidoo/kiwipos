package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Items {

  public static final List<Item> ITEMS = new ArrayList<>();
  public static final Map<Item,Integer> CHECKOUT_ITEMS = new HashMap<>();

  public static void addItem(Item item) {
    ITEMS.add(item);
  }

  public static Item createItem(String id, String name, String price) {
    return new Item(id, name, price);
  }

  public static class Item {
    public final String id;
    public final String itemName;
    public final String itemPrice;

    Item(String id, String itemName, String itemPrice) {
      this.id = id;
      this.itemName = itemName;
      this.itemPrice = itemPrice;
    }

    @Override
    public String toString() {
      return itemName;
    }
  }

  public static void addToCheckout(Item itemToAdd){
    if (!CHECKOUT_ITEMS.containsKey(itemToAdd)) {
      CHECKOUT_ITEMS.put(itemToAdd,1);
    }
    else {
      CHECKOUT_ITEMS.put(itemToAdd, CHECKOUT_ITEMS.get(itemToAdd)+1);
    }
  }

}
