package com.example.misteryshopper.activity

import android.app.Application
import com.example.misteryshopper.utils.notification.NotificationHandler

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Create notification channels on app startup
        val notificationHandler = NotificationHandler(this)
        notificationHandler.createNotificationChannel()
    }

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }
}
