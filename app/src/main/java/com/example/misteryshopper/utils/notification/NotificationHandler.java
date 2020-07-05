package com.example.misteryshopper.utils.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.misteryshopper.R;
import com.example.misteryshopper.activity.MyApplication;
import com.example.misteryshopper.activity.ShopperProfileActivity;

public class NotificationHandler {


    public static final int NOTIFICATION_ID = 0;
    private static NotificationManager notificationManager;

   public static void displayNotificationShopper(Context context, String title, String place, String when, String fee, String eName, String id) {
       RemoteViews collapsedView = new RemoteViews(context.getPackageName(),R.layout.notification_shopper_layout);
        collapsedView.setTextViewText(R.id.content_title_collapsed,title);
        collapsedView.setTextViewText(R.id.notification_ename_collapsed,eName);
        collapsedView.setTextViewText(R.id.timestamp,DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));

       RemoteViews expandedView = new RemoteViews(context.getPackageName(),R.layout.notification_shopper_layout_expanse);
       expandedView.setTextViewText(R.id.content_title_expanded,title);
       expandedView.setTextViewText(R.id.notification_ename,eName);
       expandedView.setTextViewText(R.id.notification_place,place);
       expandedView.setTextViewText(R.id.notification_when,when);
       expandedView.setTextViewText(R.id.notification_fee,fee);
       expandedView.setTextViewText(R.id.timestamp_expanded,DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));

       Intent acceptIntent = new Intent(context, NotificationIntentHandler.class);
       acceptIntent.setAction("accept");
       acceptIntent.putExtra("id",id);
       expandedView.setOnClickPendingIntent(R.id.accept_button,PendingIntent.getService(context,0,acceptIntent,PendingIntent.FLAG_UPDATE_CURRENT));

       Intent declineIntent = new Intent(context,NotificationIntentHandler.class);
       declineIntent.setAction("decline");
       declineIntent.putExtra("id",id);
       expandedView.setOnClickPendingIntent(R.id.decline_button,PendingIntent.getService(context,1,declineIntent,PendingIntent.FLAG_UPDATE_CURRENT));

       Intent showIntent = new Intent(context,NotificationIntentHandler.class);
       showIntent.setAction("show");
       showIntent.putExtra("id",id);
       expandedView.setOnClickPendingIntent(R.id.show_button,PendingIntent.getService(context,2,showIntent,PendingIntent.FLAG_UPDATE_CURRENT));
       NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
               .setSmallIcon(R.drawable.i_notifiation)
               .setCustomContentView(collapsedView)
               .setCustomBigContentView(expandedView)
               .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               .setAutoCancel(true)
               .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
       notificationManager.notify(NOTIFICATION_ID,builder.build());
   }

    public static void displayNotificationEmployer(Context context, String title, String sName,String outcome) {
        RemoteViews customView = new RemoteViews(context.getPackageName(),R.layout.notification_employer_layout);
         customView.setTextViewText(R.id.title_response,title);
         customView.setTextViewText(R.id.notification_sname,sName);
         customView.setTextViewText(R.id.notification_response,outcome);
         customView.setTextViewText(R.id.timestamp_employer,DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.i_notifiation)
                .setCustomBigContentView(customView)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    public static void createNotificationChannel(Context context) {
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           NotificationChannel notificationChannel = new NotificationChannel(MyApplication.PRIMARY_CHANNEL_ID,
                   "channel 1", NotificationManager.IMPORTANCE_HIGH);
           notificationChannel.enableLights(true);
           notificationChannel.setLightColor(Color.RED);
           notificationChannel.enableVibration(true);
           notificationChannel.setDescription("Notification from Mascot");
           notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
           notificationManager.createNotificationChannel(notificationChannel);
       }
    }




    public static class NotificationReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
