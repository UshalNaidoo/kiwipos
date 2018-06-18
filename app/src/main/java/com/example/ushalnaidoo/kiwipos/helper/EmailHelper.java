package com.example.ushalnaidoo.kiwipos.helper;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by ushal.naidoo on 15/06/18.
 */

public class EmailHelper {

  /**
   * Starts an Android email intent.
   * The activity, subject, and bodyText fields are required.
   * You can pass a null field for the chooserTitle, to, cc, and bcc fields if
   * you don't want to specify them.
   */
  public static void emailResultsToUser(Activity activity, String bodyText, String subject) {
    Intent mailIntent = new Intent();
    mailIntent.setAction(Intent.ACTION_SEND);
    mailIntent.setType("message/rfc822");
    mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    mailIntent.putExtra(Intent.EXTRA_TEXT, bodyText);

    //        if (null != null) {
    //            mailIntent.putExtra(Intent.EXTRA_EMAIL, (String[]) null);
    //        }
    //        if (null != null) {
    //            mailIntent.putExtra(Intent.EXTRA_CC, (String[]) null);
    //        }
    //        if (null != null) {
    //            mailIntent.putExtra(Intent.EXTRA_BCC, (String[]) null);
    //        }
    String[] bcc = new String[1];
    bcc[0] = "ushal7naidoo@gmail.com";
    mailIntent.putExtra(Intent.EXTRA_BCC, bcc);
    String[] send = new String[1];
    send[0] = "bramha7naidoo@hotmail.com";
    mailIntent.putExtra(Intent.EXTRA_EMAIL, send);
    activity.startActivity(Intent.createChooser(mailIntent, subject));
  }
}
