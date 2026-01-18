package com.example.misteryshopper.utils.notification

import android.util.Log
import com.example.misteryshopper.R
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.utils.SharedPrefConfig
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MyNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "onMessageReceived: remote message data: ${remoteMessage.data}")

        val data = remoteMessage.data
        val title = data[KEY_TITLE]

        when (title) {
            getString(R.string.notification_of_employment) -> {
                handleShopperNotification(data)
            }
            getString(R.string.response_notification) -> {
                handleEmployerNotification(data)
            }
        }
    }

    private fun handleShopperNotification(data: Map<String, String>) {
        val notificationId = Random().nextInt(100)
        NotificationHandler.displayNotificationShopper(
            applicationContext,
            data[KEY_TITLE],
            data["place"],
            data["when"],
            data["fee"],
            data["eName"],
            data["id"],
            data["hId"],
            data["storeId"],
            notificationId
        )
    }

    private fun handleEmployerNotification(data: Map<String, String>) {
        val notificationId = Random().nextInt(100)
        NotificationHandler.displayNotificationEmployer(
            applicationContext,
            data[KEY_TITLE],
            data["sName"],
            data["outcome"],
            notificationId
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // If a user is logged in, update their token in the database.
        val preferences = SharedPrefConfig(this)
        val loggedUser = preferences.readLoggedUser()
        if (loggedUser != null) {
            loggedUser.token = token
            GlobalScope.launch {
                try {
                    FirebaseDBHelper.instance.updateUsers(loggedUser, loggedUser.id!!, applicationContext)
                    Log.d(TAG, "Token updated successfully for user ${loggedUser.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update token", e)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MyNotificationService"
        private const val KEY_TITLE = "title"
    }
}
