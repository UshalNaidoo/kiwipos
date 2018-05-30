package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Items {

  public static final List<Item> ITEMS = new ArrayList<>();
  public static final Map<Item,Integer> CHECKOUT_ITEMS = new LinkedHashMap<>();

  public static void addItem(Item item) {
    ITEMS.add(item);
  }
  public static void replaceItems(List<Item> items) {
    ITEMS.clear();
    ITEMS.addAll(items);
  }

  public static Item createItem(String id, String name, String price, Boolean hasSubItems) {
    return new Item(id, name, price, hasSubItems);
  }

  public static class Item {
    public final String id;
    public final String itemName;
    public final String itemPrice;
    public final Boolean hasSubItems;
    public static final List<Item> subItems = new ArrayList<>();

    Item(String id, String itemName, String itemPrice, Boolean hasSubItems) {
      this.id = id;
      this.itemName = itemName;
      this.itemPrice = itemPrice;
      this.hasSubItems = hasSubItems;
    }

    public static void buildSubItems(Item subItem) {
      subItems.add(subItem);
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
  public static void subtractFromCheckout(Item itemToSubtract){
    if (!(CHECKOUT_ITEMS.get(itemToSubtract)>1)) {
      CHECKOUT_ITEMS.remove(itemToSubtract);
    }
    else {
      CHECKOUT_ITEMS.put(itemToSubtract,CHECKOUT_ITEMS.get(itemToSubtract)-1);
    }
  }
  public static void removeFromCheckout(Item itemToSubtract){
    CHECKOUT_ITEMS.remove(itemToSubtract);
  }
}
