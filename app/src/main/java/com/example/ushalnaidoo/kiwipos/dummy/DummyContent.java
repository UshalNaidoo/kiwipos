package com.example.ushalnaidoo.kiwipos.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class DummyContent {

  /**
   * An array of sample (dummy) items.
   */
  public static final List<DummyItem> ITEMS = new ArrayList<>();

  public static List<DummyItem> getTestItems() {
    List<DummyItem> ITEMS = new ArrayList<>();
    ITEMS.add(createDummyItem("11", "blah"));
    ITEMS.add(createDummyItem("2211", "as"));
    ITEMS.add(createDummyItem("1122", "asda"));
    ITEMS.add(createDummyItem("1133", "wdwd"));
    ITEMS.add(createDummyItem("1144", "qqwqw"));
    return ITEMS;
  }
  /**
   * A map of sample (dummy) items, by ID.
   */
  public static final Map<String, DummyItem> ITEM_MAP = new HashMap<>();

  public static void addItem(DummyItem item) {
    ITEMS.add(item);
    ITEM_MAP.put(item.id, item);
  }

  public static DummyItem createDummyItem(String id, String name) {
    return new DummyItem(id, name, name);
  }

  /**
   * A dummy item representing a piece of content.
   */
  public static class DummyItem {

    public final String id;

    public final String content;

    public final String details;

    DummyItem(String id, String content, String details) {
      this.id = id;
      this.content = content;
      this.details = details;
    }

    @Override
    public String toString() {
      return content;
    }
  }

}
