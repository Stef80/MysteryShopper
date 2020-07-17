package com.example.misteryshopper.activity;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.notification.NotificationHandler;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.mysteryshopper.activity.ACTION_UPDATE_NOTIFICATION";
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationHandler.createNotificationChannel(this);

    }
}
