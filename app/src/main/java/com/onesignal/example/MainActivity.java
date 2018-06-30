package com.onesignal.example;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      final TextView textView = findViewById(R.id.debug_view);
      textView.setText("OneSignal is Ready!");

      /* Deprecated, use getPermissionSubscriptionState, addPermissionObserver, or add SubscriptionObserver instead
      OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
         @Override
         public void idsAvailable(String userId, String registrationId) {
            String text = "OneSignal UserID:\n" + userId + "\n\n";

            if (registrationId != null)
               text += "Google Registration Id:\n" + registrationId;
            else
               text += "Google Registration Id:\nCould not subscribe for push";

            TextView textView = (TextView)findViewById(R.id.debug_view);
            textView.setText(text);
         }
      });
      */
      Button onSendTagsButton = findViewById(R.id.send_tags_button);
      onSendTagsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            JSONObject tags = new JSONObject();
            try {
               tags.put("key1", "value1");
               tags.put("user_name", "Jon");
            } catch (JSONException e) {
               e.printStackTrace();
            }
            OneSignal.sendTags(tags);
            textView.setText("Tags sent!");
         }

      });

      Button onGetTagsButton = findViewById(R.id.get_tags_button);
      onGetTagsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            final Collection<String> receivedTags = new ArrayList<>();
            OneSignal.getTags(new OneSignal.GetTagsHandler() {
               @Override
               public void tagsAvailable(final JSONObject tags) {
                  Log.d("debug", tags.toString());
                  new Handler(Looper.getMainLooper()).post(new Runnable() {
                     @Override
                     public void run() {
                        receivedTags.add(tags.toString());
                        textView.setText("Tags Received: " + receivedTags);
                     }
                  });
               }
            });
         }
      });

      Button onDeleteOrUpdateTagsButton = findViewById(R.id.delete_update_tags_button);
      onDeleteOrUpdateTagsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            OneSignal.getTags(new OneSignal.GetTagsHandler() {
               @Override
               public void tagsAvailable(final JSONObject tags) {
                  Log.d("debug", "Current Tags on User: " + tags.toString());
                  OneSignal.deleteTag("key1");
                  OneSignal.sendTag("updated_key", "updated_value");
                  new Handler(Looper.getMainLooper()).post(new Runnable() {
                     @Override
                     public void run() {
                        textView.setText("Updated Tags: " + tags);
                     }
                  });
               }
            });
         }
      });

      Button onGetIDsAvailableButton = findViewById(R.id.get_ids_available_button);
      onGetIDsAvailableButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            boolean isEnabled = status.getPermissionStatus().getEnabled();
            boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();
            boolean subscriptionSetting = status.getSubscriptionStatus().getUserSubscriptionSetting();

            String userID = status.getSubscriptionStatus().getUserId();
            String pushToken = status.getSubscriptionStatus().getPushToken();

            textView.setText("PlayerID: " + userID + "\nPushToken: " + pushToken);
         }
      });

      Button onPromptLocationButton = (Button)(findViewById(R.id.prompt_location_button));
      onPromptLocationButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View view) {
            OneSignal.promptLocation();
            /*
            Make sure you have one of the following permissions in your AndroidManifest.xml
            <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
             */
         }
      });

      Button onSendNotification1 = findViewById(R.id.send_notification_button);
      onSendNotification1.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            String userId = status.getSubscriptionStatus().getUserId();
            boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();

            textView.setText("Subscription Status, is subscribed:" + isSubscribed);

            if (!isSubscribed)
               return;

            try {
               JSONObject notificationContent = new JSONObject("{'contents': {'en': 'The notification message or body'}," +
                       "'include_player_ids': ['" + userId + "'], " +
                       "'headings': {'en': 'Notification Title'}, " +
                       "'big_picture': 'http://i.imgur.com/DKw1J2F.gif'}");
               OneSignal.postNotification(notificationContent, null);
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      });

      Button onSendNotification2 = findViewById(R.id.send_notification_button2);
      onSendNotification2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            String userID = status.getSubscriptionStatus().getUserId();
            boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();

            textView.setText("Subscription Status, is subscribed:" + isSubscribed);

            if (!isSubscribed)
               return;


             new Thread(new Runnable() {
                 public void run() {
                     try {
                         String jsonResponse;

                         URL url = new URL("https://onesignal.com/api/v1/notifications");
                         HttpURLConnection con = (HttpURLConnection)url.openConnection();
                         con.setUseCaches(false);
                         con.setDoOutput(true);
                         con.setDoInput(true);

                         con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                         con.setRequestProperty("Authorization", "Basic NGU2OGZkNTAtNWQ5OC00YWYwLTkwY2YtZjc1ZWNlNTFkZDk0");
                         con.setRequestMethod("POST");

                         String strJsonBody = "{"
                                 +   "\"app_id\": \"3655f39d-568c-483b-acfa-54669a55017f\","
                                 +   "\"include_segments\": [\"Fullscreen Not\"],"
                                 +   "\"data\": {\"foo\": \"bar\"},"
                                 +   "\"contents\": {\"en\": \"English Message\"}"
                                 + "}";


                         System.out.println("strJsonBody:\n" + strJsonBody);

                         byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                         con.setFixedLengthStreamingMode(sendBytes.length);

                         OutputStream outputStream = con.getOutputStream();
                         outputStream.write(sendBytes);

                         int httpResponse = con.getResponseCode();
                         System.out.println("httpResponse: " + httpResponse);

                         if (  httpResponse >= HttpURLConnection.HTTP_OK
                                 && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                             Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                             jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                             scanner.close();
                         }
                         else {
                             Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                             jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                             scanner.close();
                         }
                         System.out.println("jsonResponse:\n" + jsonResponse);

                     } catch(Throwable t) {
                         t.printStackTrace();
                     }
                     }
                 }).start();
             }




      });

      final Switch onSetSubscriptionSwitch = findViewById(R.id.set_subscription_switch);
      onSetSubscriptionSwitch.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (onSetSubscriptionSwitch.isChecked()) {
               OneSignal.setSubscription(true);
               textView.setText("User CAN receive notifications if turned on in Phone Settings");
            }
            else {
               OneSignal.setSubscription(false);
               textView.setText("User CANNOT receive notifications, even if they are turned on in Phone Settings");
            }
         }
      });
   }
}
