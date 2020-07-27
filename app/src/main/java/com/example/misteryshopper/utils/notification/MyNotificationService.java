package com.example.misteryshopper.utils.notification;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.misteryshopper.R;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyNotificationService extends FirebaseMessagingService {

    private static DBHelper mDBHelper = FirebaseDBHelper.getInstance();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        if(remoteMessage != null){
            Map<String, String> body = remoteMessage.getData();
            String title = body.get("title");
            if(title.equals(getString(R.string.notification_of_employment))) {

                NotificationHandler.displayNotificationShopper(getApplicationContext(), title, body.get("place"),
                        body.get("when"), body.get("fee"), body.get("eName"),body.get("id"),body.get("hId"),body.get("storeId"));
            }
            if(title.equals(getString(R.string.response_notification))){
             //   Map<String,String> body = remoteMessage.getData();
                NotificationHandler.displayNotificationEmployer(getApplicationContext(),title,body.get("sName"),body.get("outcome"));
            }
        }
    }


        @Override
    public void onNewToken(@NonNull String s) {
            SharedPrefConfig preferences = new SharedPrefConfig(this);
            if(preferences.readLoggedUser()!= null) {
                String token = preferences.readLoggedUser().getToken();
                FirebaseMessaging.getInstance().subscribeToTopic(token)
                        .addOnCompleteListener(x -> Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG));
            }else{
                FirebaseMessaging.getInstance().subscribeToTopic(s)
                        .addOnCompleteListener(x -> Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG));
            }
    }

}
