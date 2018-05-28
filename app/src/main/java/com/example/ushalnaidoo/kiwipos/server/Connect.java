package com.example.ushalnaidoo.kiwipos.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class Connect {

  public Connect() {
  }

  static String connectToServer(String URLString, String params) {
    HttpURLConnection connection;
    OutputStreamWriter request;
    URL url;
    String response = null;
    try {
      url = new URL(URLString);
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(15 * 1000);
      connection.setReadTimeout(15 * 1000);
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestMethod("POST");
      request = new OutputStreamWriter(connection.getOutputStream());
      request.write(params);
      request.flush();
      request.close();
      String line;

      InputStreamReader isr = new InputStreamReader(connection.getInputStream());
      BufferedReader reader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }

      response = sb.toString();
      isr.close();
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return response;
  }
}