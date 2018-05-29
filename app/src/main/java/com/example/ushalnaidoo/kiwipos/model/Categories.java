package com.example.ushalnaidoo.kiwipos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories {

  public static final List<Category> CATEGORIES = new ArrayList<>();
  public static final Map<String, Category> HASH_MAP = new HashMap<>();

  public static void addCategory(Category item) {
    CATEGORIES.add(item);
    HASH_MAP.put(item.id, item);
  }

  public static Category createCategory(String id, String categoryName) {
    return new Category(id, categoryName);
  }

  public static class Category {
    public final String id;
    public final String categoryName;

    Category(String id, String categoryName) {
      this.id = id;
      this.categoryName = categoryName;
    }

    @Override
    public String toString() {
      return categoryName;
    }
  }

}
