package com.example.misteryshopper.utils.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationHandler {

    private Location location;
    private Address address;
    private String cityFrom;
    private String cityTo;


    public LocationHandler() {



    }

    public Address getAddress(String cityFrom, Context context){
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(cityFrom,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses.size() > 0){
            address = addresses.get(0);
            Log.i(TAG, "getAddress: faound a location " + address.toString());
        }
        return address;
    }


    public boolean itsValidAddress(){
        return  address != null ;
    }


    public Double getDistance(String cityTo){


        return 0.0;
    }

}
