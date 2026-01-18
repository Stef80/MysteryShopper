package com.example.misteryshopper.utils.notification

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.misteryshopper.activity.DialogActivity


class NotificationReciver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(ContentValues.TAG, "onReceive: reciving intent")
        val overInent = Intent(context, DialogActivity::class.java)
        overInent.putExtras(intent.getExtras()!!)
        overInent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(overInent)
        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.cancel(1)
    }
}
