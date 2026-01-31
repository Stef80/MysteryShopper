package com.example.misteryshopper.utils.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.format.DateUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.MyApplication
import com.example.misteryshopper.activity.StoreListActivity

class NotificationHandler(private val context: Context) {
    private var notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private var notId: Int = 0

    @SuppressLint("MissingPermission")
    fun displayNotificationShopper(
        title: String?,
        place: String?,
        `when`: String?,
        fee: String?,
        eName: String?,
        id: String?,
        hId: String?,
        notificationId: Int
    ) {
        notId = notificationId

        val collapsedView = RemoteViews(context.packageName, R.layout.notification_shopper_layout).apply {
            setTextViewText(R.id.content_title_collapsed, title)
            setTextViewText(R.id.notification_ename_collapsed, eName)
            setTextViewText(
                R.id.timestamp,
                DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME)
            )
        }

        val expandedView = RemoteViews(context.packageName, R.layout.notification_shopper_layout_expanse).apply {
            setTextViewText(R.id.content_title_expanded, title)
            setTextViewText(R.id.notification_ename, eName)
            setTextViewText(R.id.notification_place, place)
            setTextViewText(R.id.notification_when, `when`)
            setTextViewText(R.id.notification_fee, fee)
            setTextViewText(
                R.id.timestamp_expanded,
                DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME)
            )
            // Image loading with Picasso/Coil into RemoteViews is complex and error-prone.
            // It's better to show the image inside the app.
        }

        val flag = PendingIntent.FLAG_IMMUTABLE

        // Actions
        expandedView.setOnClickPendingIntent(R.id.accept_button, createActionIntent("accept", id, hId, null, 0))
        expandedView.setOnClickPendingIntent(R.id.decline_button, createActionIntent("decline", id, hId, null, 1))
        expandedView.setOnClickPendingIntent(R.id.show_button, createActionIntent("show", null, null, place, 2))

        val builder = NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.i_notifiation)
            .setContentTitle(title)
            .setCustomHeadsUpContentView(collapsedView)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notId, builder.build())
    }

    private fun createActionIntent(action: String, empId: String?, hId: String?, address: String?, requestCode: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(NotificationActionWorker.KEY_EMPLOYER_ID, empId)
            putExtra(NotificationActionWorker.KEY_HIRING_ID, hId)
            putExtra(NotificationActionWorker.KEY_ADDRESS, address)
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }


    @SuppressLint("MissingPermission")
    fun displayNotificationEmployer(
        title: String?,
        sName: String?,
        outcome: String?,
        notificationId: Int
    ) {
        notId = notificationId
        val customView = RemoteViews(context.packageName, R.layout.notification_employer_layout).apply {
            setTextViewText(R.id.title_response, title)
            setTextViewText(R.id.notification_sname, sName)
            setTextViewText(R.id.notification_response, outcome)
            setTextViewText(
                R.id.timestamp_employer,
                DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME)
            )
        }
        
        val intent = Intent(context, StoreListActivity::class.java).apply {
            putExtra("notification_name", sName)
            putExtra("notification_outcome", outcome)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.i_notifiation)
            .setCustomContentView(customView)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }
    
    fun cancelNotification() {
        notificationManager.cancel(notId)
    }
    
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            MyApplication.PRIMARY_CHANNEL_ID,
            "Mystery Shopper Hirings",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Notification from Mystery Shopper"
        }
        notificationManager.createNotificationChannel(channel)
    }
}
