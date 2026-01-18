package com.example.misteryshopper.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.MyApplication
import com.example.misteryshopper.activity.StoreListActivity
import com.squareup.picasso.Picasso

object NotificationHandler {
    var notId: Int = 0
        private set
    private var notificationManager: NotificationManagerCompat? = null

    fun displayNotificationShopper(
        context: Context,
        title: String?,
        place: String?,
        `when`: String?,
        fee: String?,
        eName: String?,
        id: String?,
        hId: String?,
        uriImg: String?,
        notificationId: Int
    ) {
        notId = notificationId
        val collapsedView =
            RemoteViews(context.packageName, R.layout.notification_shopper_layout)
        collapsedView.setTextViewText(R.id.content_title_collapsed, title)
        collapsedView.setTextViewText(R.id.notification_ename_collapsed, eName)
        collapsedView.setTextViewText(
            R.id.timestamp,
            DateUtils.formatDateTime(
                context,
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
            )
        )

        val expandedView =
            RemoteViews(context.packageName, R.layout.notification_shopper_layout_expanse)
        expandedView.setTextViewText(R.id.content_title_expanded, title)
        expandedView.setTextViewText(R.id.notification_ename, eName)
        expandedView.setTextViewText(R.id.notification_place, place)
        expandedView.setTextViewText(R.id.notification_when, `when`)
        expandedView.setTextViewText(R.id.notification_fee, fee)
        expandedView.setTextViewText(
            R.id.timestamp_expanded,
            DateUtils.formatDateTime(
                context,
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
            )
        )

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        // Accept Action
        val acceptIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "accept"
            putExtra(NotificationActionWorker.KEY_EMPLOYER_ID, id)
            putExtra(NotificationActionWorker.KEY_HIRING_ID, hId)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(context, 0, acceptIntent, flag or PendingIntent.FLAG_UPDATE_CURRENT)
        expandedView.setOnClickPendingIntent(R.id.accept_button, acceptPendingIntent)

        // Decline Action
        val declineIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "decline"
            putExtra(NotificationActionWorker.KEY_EMPLOYER_ID, id)
            putExtra(NotificationActionWorker.KEY_HIRING_ID, hId)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(context, 1, declineIntent, flag or PendingIntent.FLAG_UPDATE_CURRENT)
        expandedView.setOnClickPendingIntent(R.id.decline_button, declinePendingIntent)

        // Show Action
        val showIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "show"
            putExtra(NotificationActionWorker.KEY_ADDRESS, place)
        }
        val showPendingIntent = PendingIntent.getBroadcast(context, 2, showIntent, flag or PendingIntent.FLAG_UPDATE_CURRENT)
        expandedView.setOnClickPendingIntent(R.id.show_button, showPendingIntent)


        val builder = NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.i_notifiation)
            .setContentTitle(title)
            .setCustomHeadsUpContentView(collapsedView)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notification = builder.build()
        notificationManager!!.notify(notId, notification)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if (uriImg != null) Picasso.get().load(uriImg)
                .into(expandedView, R.id.notification_img, notId, notification)
        }
    }

    fun displayNotificationEmployer(
        context: Context,
        title: String?,
        sName: String?,
        outcome: String?,
        notificationId: Int
    ) {
        notId = notificationId
        val customView =
            RemoteViews(context.packageName, R.layout.notification_employer_layout)
        customView.setTextViewText(R.id.title_response, title)
        customView.setTextViewText(R.id.notification_sname, sName)
        customView.setTextViewText(R.id.notification_response, outcome)
        customView.setTextViewText(
            R.id.timestamp_employer,
            DateUtils.formatDateTime(
                context,
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME
            )
        )
        
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val intent = Intent(context, StoreListActivity::class.java).apply {
            putExtra("notification_name", sName)
            putExtra("notification_outcome", outcome)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, flag or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, MyApplication.PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.i_notifiation)
            .setCustomContentView(customView)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        Log.i(ContentValues.TAG, "displayNotificationEmployer: notification id: $notificationId")
        notificationManager!!.notify(notificationId, builder.build())
    }


    @JvmStatic
    fun cancelNotification() {
        notificationManager!!.cancel(
            notId
        )
    }


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                MyApplication.PRIMARY_CHANNEL_ID,
                "channel 1", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.setLightColor(Color.RED)
            notificationChannel.enableVibration(true)
            notificationChannel.setDescription("Notification from Mystery Shopper")
            notificationManager = NotificationManagerCompat.from(context)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }
}
