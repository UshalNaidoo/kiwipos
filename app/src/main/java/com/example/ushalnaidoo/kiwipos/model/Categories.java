package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories {

  private static List<Category> CATEGORIES = new ArrayList<>();
  private static Map<String, Category> HASH_MAP = new HashMap<>();
  private static Map<Category,List<Items.Item>> cache = new HashMap<>();

  public static void addCategory(Category item) {
    buildCategories(item);
    buildHashMap(item.id, item);
  }

  public static void buildCategories(Category category) {
    CATEGORIES.add(category);
  }

  public static List<Category> getCategories() {
    return CATEGORIES;
  }

  public static void buildHashMap(String id ,Category category) {
    HASH_MAP.put(id, category);
  }

  public static Map<String, Category> getHashMap() {
    return HASH_MAP;
  }

  public static Category createCategory(String id, String categoryName) {
    return new Category(id, categoryName);
  }

  public static class Category {
    private String id;
    private String categoryName;

    Category(String id, String categoryName) {
      this.id = id;
      this.categoryName = categoryName;
    }

    @Override
    public String toString() {
      return categoryName;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getId() {
      return this.id;
    }

    public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
    }

    public String getCategoryName() {
      return this.categoryName;
    }
  }

  public static void addToCache(Categories.Category category, List<Items.Item> items) {
    if (!cache.containsKey(category)) {
      cache.put(category,items);
    }
  }

  public static List<Items.Item> readFromCache(Category category) {
    if (cache.containsKey(category)) {
      return cache.get(category);
    }
    return null;
  }
}
