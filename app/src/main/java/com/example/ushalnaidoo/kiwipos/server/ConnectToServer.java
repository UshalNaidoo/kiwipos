package com.example.ushalnaidoo.kiwipos.server;

import android.util.Log;

public class ConnectToServer {
  public static final String API_KEY = "AoD93128Jd73jKH31je3";
  public static final String CATEGORIES = "categories";
  public static final String ITEMS = "items";

  public static String getCategories() {
    String parameters = "key=" + API_KEY;
    String UrlString = ServerSettings.SERVER + ServerSettings.CATEGORIES;
    try {
      return "{" + CATEGORIES + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
    return null;
  }

  public static String getItemsForCategory(String groupId) {
    String parameters = "key=" + API_KEY + "&&groupid="+groupId;
    String UrlString = ServerSettings.SERVER + ServerSettings.ITEMS_FOR_CATEGORY;
    try {
      return "{" + ITEMS + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving items", e);
    }
    return null;
  }

}
