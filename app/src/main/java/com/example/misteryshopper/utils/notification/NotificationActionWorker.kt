package com.example.misteryshopper.utils.notification

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.MapsActivity
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.utils.SharedPrefConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationActionWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val dbHelper = FirebaseDBHelper.instance

    override suspend fun doWork(): Result {
        val action = inputData.getString(KEY_ACTION) ?: return Result.failure()
        val hId = inputData.getString(KEY_HIRING_ID)
        val employerId = inputData.getString(KEY_EMPLOYER_ID)
        val address = inputData.getString(KEY_ADDRESS)

        val config = SharedPrefConfig(context)
        val shopperModel = config.readLoggedUser() as? ShopperModel
        val name = shopperModel?.name
        val surname = shopperModel?.surname

        return withContext(Dispatchers.IO) {
            try {
                when (action) {
                    "accept" -> {
                        dbHelper.setOutcome(hId!!, "accepted")
                        val token = dbHelper.getTokenById(employerId!!)
                        MessageCreationService.buildMessage(
                            context,
                            token,
                            context.getString(R.string.response_notification),
                            "$name $surname",
                            "accepted"
                        )
                        NotificationHandler.cancelNotification()
                    }
                    "decline" -> {
                        dbHelper.setOutcome(hId!!, "declined")
                        val token = dbHelper.getTokenById(employerId!!)
                        MessageCreationService.buildMessage(
                            context,
                            token,
                            context.getString(R.string.response_notification),
                            "$name $surname",
                            "declined"
                        )
                        NotificationHandler.cancelNotification()
                    }
                    "show" -> {
                        val go = Intent(context, MapsActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra("address", address)
                        }
                        context.startActivity(go)
                        NotificationHandler.cancelNotification()
                    }
                }
                Result.success()
            } catch (e: Exception) {
                // You can log the error here
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_ACTION = "action"
        const val KEY_HIRING_ID = "hId"
        const val KEY_EMPLOYER_ID = "id"
        const val KEY_ADDRESS = "address"
    }
}
