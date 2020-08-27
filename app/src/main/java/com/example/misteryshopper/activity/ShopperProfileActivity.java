package com.example.misteryshopper.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.utils.MyPagerAdapter;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.camera.CircleTransform;
import com.example.misteryshopper.utils.camera.PictureHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;


public class ShopperProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String SHOPPER = "Shopper";
    private DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    private TextView name;
    private TextView surname;
    private TextView address;
    private TextView city;
    private TextView email;
    private TextView cf;
    private TextView totalAmount;
    private ImageView imgProfile;
    private ProgressBar bar;
    private Toolbar toolbar;
    private ViewPager pager;
    private LinearLayout layout;
    private LinearLayout amountLayout;
    private FragmentPagerAdapter pagerAdapter;

    private ShopperModel shopperModel;
    private SharedPrefConfig config;
    private String userMail;
    private String mail;
    private final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_profile);

        config = new SharedPrefConfig(getApplicationContext());


        toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        pager = findViewById(R.id.v_pager);
        layout = findViewById(R.id.profile_layout);
        amountLayout = findViewById(R.id.amount_layout);
        name = findViewById(R.id.profile_name);
        surname = findViewById(R.id.profile_surname);
        address = findViewById(R.id.profile_address);
        cf = findViewById(R.id.profile_cf);
        city = findViewById(R.id.profile_city);
        email = findViewById(R.id.profile_email);
        totalAmount = findViewById(R.id.profile_total_amount);
        bar = findViewById(R.id.progress_bar_profile);
        imgProfile = findViewById(R.id.profile_image);

        userMail = config.readLoggedUser().getEmail();
        mail = getIntent().getStringExtra(EMAIL);
        if (mail != null) {
            if (!userMail.equals(mail)) {
                amountLayout.setVisibility(View.GONE);
                mDBHelper.getShopperByMail(mail, (shopperList, keys) -> {
                    Log.i("SHOPPERPROFILE", shopperList.get(0).toString());
                    shopperModel = (ShopperModel) shopperList.get(0);
                    if (shopperModel != null) {
                        name.setText(shopperModel.getName());
                        surname.setText(shopperModel.getSurname());
                        address.setText(shopperModel.getAddress());
                        city.setText(shopperModel.getCity());
                        cf.setText(shopperModel.getCf());
                        email.setText(shopperModel.getEmail());
                        String imgUrl = shopperModel.getImageUri();
                        if (!TextUtils.isEmpty(imgUrl))
                            imgProfile.setVisibility(View.VISIBLE);
                            Picasso.get().load(imgUrl).transform(new CircleTransform()).into(imgProfile);
                            bar.setVisibility(View.GONE);
                    }
                });
            } else {
                shopperModel = (ShopperModel) config.readLoggedUser();
                String imgUrl = shopperModel.getImageUri();
                if (TextUtils.isEmpty(imgUrl) || imgUrl == null) {
                    bar.setVisibility(View.GONE);
                    imgProfile.setVisibility(View.VISIBLE);
                    imgProfile.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            PictureHandler.getPicture(ShopperProfileActivity.this);
                        }
                    });
                } else {
                    imgProfile.setVisibility(View.VISIBLE);
                    Picasso.get().load(imgUrl).transform(new CircleTransform()).into(imgProfile);
                    bar.setVisibility(View.GONE);
                }



                pager.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, shopperModel);
                pager.setAdapter(pagerAdapter);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureHandler.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            PictureHandler.uploadImage(shopperModel.getId(), imgProfile, bar, SHOPPER, getApplicationContext());
        }
    }

    @Override
    protected void onStart() {
        mDBHelper.getShopperByMail(userMail, new DBHelper.DataStatus() {
            @Override
            public void dataIsLoaded(List<?> obj, List<String> keys) {
                shopperModel = (ShopperModel) obj.get(0);
                Log.i(TAG, "dataIsLoaded: getTotalAmount: " + shopperModel.getTotalAmount());
                totalAmount.setText(String.valueOf(shopperModel.getTotalAmount()));
            }
        });
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        menu.removeItem(R.id.item_add);
        if (mail.equals(userMail)) {
            menu.removeItem(R.id.item_back);
        } else {
            menu.removeItem(R.id.log_out);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_back:
                Intent backIntent = new Intent(this, ShopperListActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backIntent);
                break;
            case R.id.log_out:
                mDBHelper.signOut(this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
