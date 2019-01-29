package com.eleganz.msafiri.utils;

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

import com.google.firebase.messaging.RemoteMessage;



/**
 * Created by Uv on 2/7/2018.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    long[] vib;
    SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //showNotification1(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        Log.d("mssgggggggg", "" + remoteMessage.getData().toString());

    }

    /*private void showNotification1(String title, String text) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        Intent i = new Intent(this, NavigationActivity.class);
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
                    .setSound(uri)
                    .setVibrate(new long[]{1000,500})
                    .setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.colorPrimary));

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String id = "id_product";
                // The user-visible name of the channel.
                CharSequence name = "Zahid Tractor";
                // The user-visible description of the channel.
                String description = "Zahid Tractor";
                int importance = NotificationManager.IMPORTANCE_MAX;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
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
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setNumber(1)
                        .setColor(255)
                        .setContentText(text)
                        .setWhen(System.currentTimeMillis());
                notificationManager.notify(1, notificationBuilder.build());
            }


            manager.notify(0, builder.build());


    }
    private int getNotificationIcon () {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notification_icon : R.drawable.imgpsh_fullsize;
    }*/
}
