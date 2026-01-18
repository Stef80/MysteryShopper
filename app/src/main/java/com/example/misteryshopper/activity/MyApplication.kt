package com.example.misteryshopper.activity

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.util.Log
import android.widget.Toast
import com.example.misteryshopper.utils.notification.NotificationHandler
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHandler.createNotificationChannel(this)
        isServicesOk()
    }

    fun isServicesOk(): Boolean {
        Log.i(TAG, "isServicesOk: chcking google sevices version ")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (available == ConnectionResult.SUCCESS) {
            //everithing is fine and user can make map request
            Log.d(TAG, "isServicesOk: Google Play Services is working")
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOk: an error occured but we can fix it")
            val activity = applicationContext as Activity
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST)
            dialog?.show()
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_LONG).show()
        }
        return false
    }

    companion object {
        private const val ACTION_UPDATE_NOTIFICATION = "com.example.mysteryshopper.activity.ACTION_UPDATE_NOTIFICATION"
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        private const val ERROR_DIALOG_REQUEST = 9001
        private const val TAG = "MyApplication"
    }
}
