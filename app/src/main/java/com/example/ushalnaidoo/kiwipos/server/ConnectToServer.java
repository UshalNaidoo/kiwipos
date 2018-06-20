package com.example.ushalnaidoo.kiwipos.server;

import android.util.Log;

public class ConnectToServer {
  private static final String API_KEY = "AoD93128Jd73jKH31je3";
  public static final String CATEGORIES = "categories";
  public static final String ITEMS = "items";
  public static final String SUB_ITEMS = "subitems";
  public static final String ADDONS = "addons";
  public static final String SALES = "sales";

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

  public static String getSubItemsForItem(String item_id) {
    String parameters = "key=" + API_KEY + "&&itemid="+item_id;
    String UrlString = ServerSettings.SERVER + ServerSettings.SUB_ITEMS_FOR_ITEM;
    try {
      return "{" + SUB_ITEMS + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving items", e);
    }
    return null;
  }

  public static String getAddonsForItem(String item_id) {
    String parameters = "key=" + API_KEY + "&&itemid="+item_id;
    String UrlString = ServerSettings.SERVER + ServerSettings.ADDONS_FOR_ITEM;
    try {
      return "{" + ADDONS + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving items", e);
    }
    return null;
  }

  public static void tenderSale(String notes, String amount, String takeAway, String saleItems, String time) {
    String parameters = "key=" + API_KEY + "&&notes="+notes + "&&amount="+amount + "&&takeAway="+takeAway + "&&saleItems="+saleItems + "&&time="+time;
    String UrlString = ServerSettings.SERVER + ServerSettings.TENDER_SALE;
    Connect.connectToServer(UrlString, parameters);
  }

  public static String getTodaysSales() {
    String parameters = "key=" + API_KEY;
    String UrlString = ServerSettings.SERVER + ServerSettings.TODAYSSALES;
    try {
      return "{" + SALES + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
    return null;
  }

  public static String getTenderedSales() {
    String parameters = "key=" + API_KEY;
    String UrlString = ServerSettings.SERVER + ServerSettings.TENDEREDSALES;
    try {
      return "{" + SALES + ":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
    return null;
  }

  public static int getTenderedSalesCount() {
    String parameters = "key=" + API_KEY;
    String UrlString = ServerSettings.SERVER + ServerSettings.TENDEREDSALESCOUNT;
    try {
      return Integer.parseInt(Connect.connectToServer(UrlString, parameters).split(" ")[0]);
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
    return 0;
  }

  public static void bumpOrders(String orderid) {
    String parameters = "key=" + API_KEY + "&&orderid=" + orderid;
    String UrlString = ServerSettings.SERVER + ServerSettings.BUMPORDER;
    try {
      Connect.connectToServer(UrlString, parameters);
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
  }

  public static void cashUp() {
    String parameters = "key=" + API_KEY;
    String UrlString = ServerSettings.SERVER + ServerSettings.CASHUP;
    Connect.connectToServer(UrlString, parameters);
  }

}
