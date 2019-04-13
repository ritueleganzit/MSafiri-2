package com.eleganz.msafiri.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eleganz.msafiri.HomeActivity;
import com.eleganz.msafiri.R;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Uv on 2/7/2018.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    long[] vib;
    String message="",notification_type="";
    SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        try {
            JSONObject jsonObject=new JSONObject(remoteMessage.getData().get("message")+"");
            message=jsonObject.getString("message");
            notification_type=jsonObject.getString("type");
            Log.d("mssgggggggg", "" + remoteMessage.getData().toString());


            if (notification_type.equalsIgnoreCase("user_reminder"))
            {
                showNotification1(remoteMessage.getData().get("title"), message);

            }
            else{
                showNotification1(remoteMessage.getData().get("title"), message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void showNotification1(String title, String text) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("from","notification");
        i.putExtra("title",title);
        i.putExtra("content",text);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(text)
                    .setContentTitle("MSafiri User")
                    .setSound(uri)
                    .setVibrate(new long[]{1000,500})
                    .setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.colorPrimary));

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String id = "id_product";
                // The user-visible name of the channel.
                CharSequence name = "MSafiri User";
                // The user-visible description of the channel.
                String description = text;
                int importance = NotificationManager.IMPORTANCE_MAX;
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                // Sets the notification light color for notifications posted to this
                // channel, if the device supports this feature.
                mChannel.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(mChannel);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"id_product")
                        .setSmallIcon(getNotificationIcon())//your app icon
                        .setBadgeIconType(getNotificationIcon()) //your app icon
                        .setChannelId(id)
                        .setSound(uri)
                        .setVibrate(new long[]{1000,500})
                        .setContentTitle(name)
                        .setAutoCancel(true)
                        .setNumber(1)
                        .setContentIntent(pendingIntent)
                        .setColor(255)
                        .setContentText(text)
                        .setWhen(System.currentTimeMillis());
                notificationManager.notify(1, notificationBuilder.build());
            }


            manager.notify(0, builder.build());


    }
    private int getNotificationIcon () {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.logo : R.drawable.logo;
    }
}
