package com.example.misteryshopper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.utils.DialogUIHelper;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.maps.LocationHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar = findViewById(R.id.toolbar_maps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        String addressStr =  intent.getStringExtra("address");
        Log.i(TAG, "onMapReady: address : "+ addressStr);
        Address address = new LocationHandler().getAddress(addressStr,getApplicationContext());
        if(address != null) {
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F));
            MarkerOptions options = new MarkerOptions().position(latLng);
            mMap.addMarker(options);
        }else {
            Toast.makeText(getApplicationContext(),"Error: invalid address promped",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        menu.removeItem(R.id.item_add);
        menu.removeItem(R.id.log_out);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_back:
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}