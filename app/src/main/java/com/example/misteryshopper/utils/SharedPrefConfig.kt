package com.example.misteryshopper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.midi.MidiOutputPort;
import android.util.Log;

import com.example.misteryshopper.models.EmployerModel;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.models.User;
import com.google.gson.Gson;

public class SharedPrefConfig {

    private static final String SHOPPER = "Shopper";
    private static final String EMPLOYER = "Employer";
    private static final String STORE = "store" ;
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
        return (User) formJSON(userVal);

    }

    public void writePrefString(String key , String data){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,data);
        editor.commit();
    }

    public String readPrefString(String key){
       return   preferences.getString(key, "");
    }

    private String getJSON(Object usr) {
        return g.toJson(usr);
    }

    public Object formJSON(String json) {
        if (json.contains(SHOPPER)) {
            ShopperModel model = g.fromJson(json, ShopperModel.class);
            return model;
        }else if(json.contains(EMPLOYER)) {
            EmployerModel model =  g.fromJson(json, EmployerModel.class);
            return model;
        } else {
          return null;
        }

    }

    private StoreModel fromJSONToStore(String json){
        StoreModel storeModel = g.fromJson(json, StoreModel.class);
        return storeModel;
    }

    public void cancelData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void writPrefStore(StoreModel store) {
       preferences.edit().putString(STORE,getJSON(store)).commit();

    }

    public StoreModel readPrefStore(){
        String storeJSON = preferences.getString(STORE,"");
        return (StoreModel) fromJSONToStore(storeJSON);
    }
}
