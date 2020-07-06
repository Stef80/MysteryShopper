package com.example.misteryshopper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.midi.MidiOutputPort;
import android.util.Log;

import com.example.misteryshopper.models.EmployerModel;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.models.User;
import com.google.gson.Gson;

public class SharedPrefConfig {

    private static final String SHOPPER = "Shopper";
    private static final String EMPLOYER = "Employer";
    private final String DATA = "user_data";
    private final String USER_VALUE = "user";
   private SharedPreferences preferences;
   private Context context;
   private Gson g = new Gson();

    public SharedPrefConfig(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(DATA, context.MODE_PRIVATE);
    }

    public void writeLoggedUser(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_VALUE, getJSON(user));
        editor.commit();
    }

    public User readLoggedUser(){
        String userVal = "";
        userVal = preferences.getString(USER_VALUE,"");
        return  formJSON(userVal);

    }

    private String getJSON(User usr) {
        return g.toJson(usr);
    }

    public User formJSON(String json) {
        if (json.contains(SHOPPER)) {
            ShopperModel model = g.fromJson(json, ShopperModel.class);
            return model;
        }else if(json.contains(EMPLOYER)) {
            EmployerModel model =  g.fromJson(json, EmployerModel.class);
            return model;
        } else
            return null;
    }


    public void cancelData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
