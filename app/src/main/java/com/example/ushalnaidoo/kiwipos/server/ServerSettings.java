package com.example.ushalnaidoo.kiwipos.server;

public class ServerSettings {

  public static String SERVER = "http://creatureislandgame.com/kiwipos_api";

  static String CATEGORIES = "/getCategories.php";
  static String ITEMS_FOR_CATEGORY = "/getItemsForCategory.php";
  static String SUB_ITEMS_FOR_ITEM = "/getSubItemForItem.php";
  static String ADDONS_FOR_ITEM = "/getAddonsForItem.php";

  static String TENDER_SALE = "/tenderSale.php";

  static String TODAYSSALES = "/getTodaysSales.php";
  static String TENDEREDSALES = "/getTenderedSales.php";
  static String TENDEREDSALESCOUNT = "/getTenderedSalesCount.php";
  static String BUMPORDER = "/bumporder.php";
  static String CASHUP = "/cashup.php";
}
