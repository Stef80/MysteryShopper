package com.example.misteryshopper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.notification.NotificationHandler;

import java.util.List;

public class ShopperProfileActivity extends AppCompatActivity {

private DBHelper mDBHelper = FirebaseDBHelper.getInstance();
private TextView name;
private TextView surname;
private TextView address;
private TextView city;
private TextView email;
private TextView cf;
private ImageView imgProfile;
private Toolbar toolbar;
private ViewPager pager;
private LinearLayout layout;

private final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_profile);

        toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        pager = findViewById(R.id.v_pager);
        layout = findViewById(R.id.profile_layout);
        name = findViewById(R.id.profile_name);
        surname = findViewById(R.id.profile_surname);
        address = findViewById(R.id.profile_address);
        cf = findViewById(R.id.profile_cf);
        city = findViewById(R.id.profile_city);
        email = findViewById(R.id.profile_email);
        String userMail = new SharedPrefConfig(getApplicationContext()).readLoggedUser().getEmail();
        String mail = getIntent().getStringExtra(EMAIL);
        Log.i("SHOPPERPROFILEMAIL", mail);
        if (mail != null) {
            if (!userMail.equals(mail)){
                mDBHelper.getShopperByMail(mail, (shopperList, keys) -> {
                    Log.i("SHOPPERPROFILE", shopperList.get(0).toString());
                    ShopperModel shopperModel = (ShopperModel) shopperList.get(0);
                    if (shopperModel != null) {
                        name.setText(shopperModel.getName());
                        surname.setText(shopperModel.getSurname());
                        address.setText(shopperModel.getAddress());
                        city.setText(shopperModel.getCity());
                        cf.setText(shopperModel.getCf());
                        email.setText(shopperModel.getEmail());
                    }
                });
        }else {
                pager.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);

            }
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add:
                findViewById(R.id.item_add).setVisibility(View.GONE);
             break;
            case R.id.log_out:
                mDBHelper.signOut(this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
