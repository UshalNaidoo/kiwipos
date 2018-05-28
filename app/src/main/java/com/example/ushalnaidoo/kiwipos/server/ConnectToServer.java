package com.example.ushalnaidoo.kiwipos.server;

import android.util.Log;

public class ConnectToServer {

  public static String getCategories() {
    String parameters = "key=AoD93128Jd73jKH31je3";
    String UrlString = ServerSettings.SERVER + ServerSettings.CATEGORIES;
    try {
      return "{\"categories\":" + Connect.connectToServer(UrlString, parameters) + "}";
    } catch (Exception e) {
      Log.e("KiwiPos", "Error when retrieving categories", e);
    }
    return null;
  }

}
