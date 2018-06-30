package com.onesignal.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;

public class NotificationExtenderExample extends NotificationExtenderService {
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
      Log.d("OneSignalExample", "Inside onNotificationProcessing");
       // Read Properties from result
      OverrideSettings overrideSettings = new OverrideSettings();
      overrideSettings.extender = new NotificationCompat.Extender() {
         @Override
         public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
            // Sets the background notification color to Red on Android 5.0+ devices.
            return builder.setColor(new BigInteger("FFFF0000", 16).intValue());
         }
      };

      OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
      Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

      Intent i = new Intent(this, GreenActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

      PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
              .setAutoCancel(true)
              .setContentTitle("FCM Test")
              .setContentText("Test by jewel")
              .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
              .setContentIntent(pendingIntent);
      builder.setFullScreenIntent(pendingIntent, true);

      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

      manager.notify(0,builder.build());
      // Return true to stop the notification from displaying
      return true;
   }
}
