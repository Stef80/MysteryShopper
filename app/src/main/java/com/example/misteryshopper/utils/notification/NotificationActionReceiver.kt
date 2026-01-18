package com.example.misteryshopper.utils.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workManager = WorkManager.getInstance(context)

        val data = Data.Builder()
            .putString(NotificationActionWorker.KEY_ACTION, intent.action)
            .putString(NotificationActionWorker.KEY_HIRING_ID, intent.getStringExtra(NotificationActionWorker.KEY_HIRING_ID))
            .putString(NotificationActionWorker.KEY_EMPLOYER_ID, intent.getStringExtra(NotificationActionWorker.KEY_EMPLOYER_ID))
            .putString(NotificationActionWorker.KEY_ADDRESS, intent.getStringExtra(NotificationActionWorker.KEY_ADDRESS))
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationActionWorker>()
            .setInputData(data)
            .build()

        workManager.enqueue(workRequest)
    }
}
