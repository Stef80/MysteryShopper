package com.example.misteryshopper.activity;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.notification.NotificationHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.ContentValues.TAG;

public class MyApplication extends Application {
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.mysteryshopper.activity.ACTION_UPDATE_NOTIFICATION";
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationHandler.createNotificationChannel(this);
        isServicesOk();


    }
    
    public boolean isServicesOk(){
        Log.i(TAG, "isServicesOk: chcking google sevices version ");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if(available == ConnectionResult.SUCCESS){
            //everithing is fine and user can make map request
            Log.d(TAG, "isServicesOk: Google Play Services is working");
        }else if( GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOk: an error occured but we can fix it");
            Activity activity = (Activity) getApplicationContext();
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this,"You can't make map request",Toast.LENGTH_LONG).show();
        }
       return false;
    }


}
