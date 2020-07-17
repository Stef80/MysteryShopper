package com.example.misteryshopper.utils.notification;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.misteryshopper.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MessageCreationService {

    private static final String SERVER_KEY = "AAAAJB3_Ass:APA91bGlQIvYUJHaFKCTcxX8ILKpBNUERP67-kimvzgLXIvwVNQHOrSCIB_CRdJuDa2-V12K_40uRbvrWMlFuuATm6I6I2GyZug0MvhAs3KK-_GFAktIj_rkZC1IFzA6EiJu5C-qkzKL";
    private static final String CONTENT_TYPE ="application/json";
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";


    public static void buildMessage(Context context,String token, String notificationTitle,String message,String response){
          buildMessage(context, token, notificationTitle, message,null, response,null,null,null,null);
    }

    public static void buildMessage(Context context,String token, String notificationTitle, String place , String when, String fee, String eName,String empId,String idHiring,String storeId) {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();
        try {
            notification.put("to","topics/"+token);
            body.put("title", notificationTitle);
            if(notificationTitle.equals(context.getString(R.string.response_notification))){
                body.put("sName", place);
                body.put("outcome",fee);
            }else {
                body.put("place", place);
                body.put("hId",idHiring);
                body.put("storeId",storeId);
                body.put("id",empId);
                body.put("when", when);
                body.put("fee", fee);
                body.put("eName", eName);
            }
            notification.put("notification",body);
            notification.put("data",body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(context,notification);
    }

    private static void sendNotification(Context context,JSONObject notification) {
        JsonObjectRequest request = new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                Log.i(TAG, error.networkResponse.headers.toString());
                }else {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                }
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String,String> params = new HashMap<>();
                params.put("Authorization","key="+ SERVER_KEY);
                params.put("Content-Type",CONTENT_TYPE);
               Log.i("SERVICE", params.toString());
               return params;
            }
        };

     MySingleton.getInstance(context).addToRequestQueue(request);
    }
}
