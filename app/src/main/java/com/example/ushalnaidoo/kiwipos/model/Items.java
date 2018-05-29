package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Items {

  public static final List<Item> ITEMS = new ArrayList<>();
  public static final Map<String, Item> ITEM_MAP = new HashMap<>();

  public static void addItem(Item item) {
    ITEMS.add(item);
    ITEM_MAP.put(item.id, item);
  }

  public static Item createItem(String id, String name, String price) {
    return new Item(id, name, price);
  }

  public static List<Item> getTestItems() {
    List<Item> testItems = new ArrayList<>();
    testItems.add(createItem("12","Cat1", "1200"));
    return testItems;
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

}
