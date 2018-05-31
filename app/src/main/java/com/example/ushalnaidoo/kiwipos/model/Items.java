package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Items {

  public static final List<Item> ITEMS = new ArrayList<>();
  public static final Map<CheckoutItem,Integer> CHECKOUT_ITEMS = new LinkedHashMap<>();

  public static void addItem(Item item) {
    ITEMS.add(item);
  }
  public static void replaceItems(List<Item> items) {
    ITEMS.clear();
    ITEMS.addAll(items);
  }

  public static Item createItem(String id, String name, String price, Boolean hasSubItems, Boolean hasAddons) {
    return new Item(id, name, price, hasSubItems, hasAddons);
  }

  public static void addToCheckout(CheckoutItem itemToAdd){
    Boolean addToExisting = false;
    CheckoutItem checkoutItem = null;
    for (Map.Entry<Items.CheckoutItem, Integer> entry : Items.CHECKOUT_ITEMS.entrySet()) {
      checkoutItem = entry.getKey();
      if (checkoutItem.getId().equals(itemToAdd.getId())
         && checkoutItem.getAssignedAddons().equals(itemToAdd.getAssignedAddons())
         && checkoutItem.getItemName().equals(itemToAdd.getItemName())) {
        addToExisting = true;
        break;
      }
    }
    if (!addToExisting) {
      CHECKOUT_ITEMS.put(itemToAdd,1);
    }
    else {
      CHECKOUT_ITEMS.put(checkoutItem, CHECKOUT_ITEMS.get(checkoutItem)+1);
    }
  }
  public static void subtractFromCheckout(CheckoutItem itemToSubtract){
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

  public static class Item {
    private String id;
    private String itemName;
    private String itemPrice;
    private Boolean subItemsExist;
    private Boolean addonsExist;
    private List<Item> subItems = new ArrayList<>();
    private List<Addons.Addon> addons = new ArrayList<>();

    Item(String id, String itemName, String itemPrice, Boolean subItemsExist, Boolean addonsExist) {
      this.id = id;
      this.itemName = itemName;
      this.itemPrice = itemPrice;
      this.subItemsExist = subItemsExist;
      this.addonsExist = addonsExist;
    }

    public void buildSubItems(Item subItem) {
      this.subItems.add(subItem);
    }

    public List<Item> getSubItems() {
      return this.subItems;
    }

    public void buildAddons(Addons.Addon addon) {
      this.addons.add(addon);
    }

    public List<Addons.Addon> getAddons() {
      return this.addons;
    }

    @Override
    public String toString() {
      return itemName;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getItemName() {
      return itemName;
    }

    public void setItemName(String itemName) {
      this.itemName = itemName;
    }

    public String getItemPrice() {
      return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
      this.itemPrice = itemPrice;
    }

    public Boolean getSubItemsExist() {
      return subItemsExist;
    }

    public void setSubItemsExist(Boolean subItemsExist) {
      this.subItemsExist = subItemsExist;
    }

    public Boolean getAddonsExist() {
      return addonsExist;
    }

    public void setAddonsExist(Boolean addonsExist) {
      this.addonsExist = addonsExist;
    }
  }

  public static class CheckoutItem extends Item {
    private List<Addons.Addon> assignedAddons = new ArrayList<>();

    CheckoutItem(String id, String itemName, String itemPrice, Boolean subItemsExist, Boolean addonsExist) {
      super(id, itemName, itemPrice, subItemsExist, addonsExist);
    }
    public CheckoutItem(Item item) {
      super(item.id, item.itemName, item.itemPrice, item.subItemsExist, item.addonsExist);
    }

    public void buildAssignedAddons(Addons.Addon addon) {
      this.assignedAddons.add(addon);
    }
    public void removeAssignedAddons(Addons.Addon addon) {
      this.assignedAddons.remove(addon);
    }

    public List<Addons.Addon> getAssignedAddons() {
      return this.assignedAddons;
    }

  }
}
