package com.example.misteryshopper.utils.notification

import android.util.Log
import com.example.misteryshopper.R
import com.example.misteryshopper.repository.UserRepository
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
        val notificationHandler = NotificationHandler(applicationContext)

        when (title) {
            getString(R.string.notification_of_employment) -> {
                handleShopperNotification(notificationHandler, data)
            }
            getString(R.string.response_notification) -> {
                handleEmployerNotification(notificationHandler, data)
            }
        }
    }

    private fun handleShopperNotification(handler: NotificationHandler, data: Map<String, String>) {
        val notificationId = Random().nextInt(100)
        handler.displayNotificationShopper(
            title = data[KEY_TITLE],
            place = data["place"],
            `when` = data["when"],
            fee = data["fee"],
            eName = data["eName"],
            id = data["id"],
            hId = data["hId"],
            notificationId = notificationId
        )
    }

    private fun handleEmployerNotification(handler: NotificationHandler, data: Map<String, String>) {
        val notificationId = Random().nextInt(100)
        handler.displayNotificationEmployer(
            title = data[KEY_TITLE],
            sName = data["sName"],
            outcome = data["outcome"],
            notificationId = notificationId
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        val loggedUser = SharedPrefConfig(this).readLoggedUser()
        if (loggedUser != null) {
            GlobalScope.launch {
                try {
                    UserRepository.updateUserToken(loggedUser, token)
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
