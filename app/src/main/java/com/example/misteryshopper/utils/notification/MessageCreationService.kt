package com.example.misteryshopper.utils.notification

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.misteryshopper.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object MessageCreationService {

    private const val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private const val TAG = "MessageCreationService"

    // Overloaded function for simple response messages
    suspend fun buildMessage(
        context: Context,
        token: String?,
        notificationTitle: String,
        message: String?,
        response: String?
    ) {
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "FCM Token is null or empty. Cannot send notification.")
            return
        }
        val body = JSONObject().apply {
            put("title", notificationTitle)
            put("sName", message)
            put("outcome", response)
        }
        val notification = JSONObject().apply {
            put("to", token)
            put("data", body)
        }
        sendNotification(context, notification)
    }

    // Main function for detailed hiring messages
    suspend fun buildMessage(
        context: Context,
        token: String?,
        notificationTitle: String,
        place: String?,
        `when`: String?,
        fee: String?,
        eName: String?,
        empId: String?,
        idHiring: String?,
        storeId: String?
    ) {
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "FCM Token is null or empty. Cannot send notification.")
            return
        }
        val body = JSONObject().apply {
            put("title", notificationTitle)
            put("place", place)
            put("hId", idHiring)
            put("storeId", storeId)
            put("id", empId)
            put("when", `when`)
            put("fee", fee)
            put("eName", eName)
        }
        val notification = JSONObject().apply {
            put("to", token)
            put("data", body)
        }
        sendNotification(context, notification)
    }

    private suspend fun sendNotification(context: Context, notification: JSONObject) = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine<JSONObject> { continuation ->
            val request = object : JsonObjectRequest(Method.POST, FCM_API, notification,
                { response -> continuation.resume(response) },
                { error -> continuation.resumeWithException(error) }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    return mapOf(
                        "Authorization" to "key=${context.getString(R.string.notification_server_key)}",
                        "Content-Type" to "application/json"
                    )
                }
            }
            MySingleton.getInstance(context).addToRequestQueue(request)
        }
    }
}
