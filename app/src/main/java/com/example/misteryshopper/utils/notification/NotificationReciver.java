package com.example.misteryshopper.utils.notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.misteryshopper.activity.DialogActivity;


import static android.content.ContentValues.TAG;


public class NotificationReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: reciving intent");
        Intent overInent = new Intent(context, DialogActivity.class);
        overInent.putExtras(intent.getExtras());
        overInent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(overInent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.cancel(1);

    }
}
