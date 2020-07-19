package com.example.misteryshopper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.utils.DialogUIHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.utils.RecyclerViewConfig;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.example.misteryshopper.utils.camera.PictureHandler;


import java.util.List;

public class StoreListActivity extends AppCompatActivity {

    private DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<StoreModel> storeModelList;
    private TextView textEmpty;
    String userID;

    public StoreListActivity getReference() {
        return StoreListActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        userID = new SharedPrefConfig(getApplicationContext()).readLoggedUser().getId();
        recyclerView = findViewById(R.id.recyclerView_shopper);
        textEmpty = findViewById(R.id.emptyState);
            mDBHelper.readStoreOfSpecificUser(userID, new DBHelper.DataStatus() {
                @Override
                public void dataIsLoaded(List<?> obj, List<String> keys) {
                    if(obj.isEmpty()) {
                        textEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        new RecyclerViewConfig(null).setConfigList(recyclerView, StoreListActivity.this, (List<StoreModel>) obj,
                                keys, null);
                        textEmpty.setVisibility(View.GONE);
                    }
                }
            });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureHandler.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent idIntent = getIntent();
            String idStore = idIntent.getStringExtra("store_id");
            PictureHandler.uploadImage(idStore,null,"Store",getApplicationContext());
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mDBHelper.readStoreOfSpecificUser(userID, new DBHelper.DataStatus() {
//            @Override
//            public void dataIsLoaded(List<?> obj, List<String> keys) {
//                if(obj.isEmpty()) {
//                    textEmpty.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                } else {
//                    new RecyclerViewConfig(null).setConfigList(recyclerView, StoreListActivity.this, (List<StoreModel>) obj,
//                            keys, null);
//                    textEmpty.setVisibility(View.GONE);
//                }
//            }
//        });
 //   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add:
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        StoreModel model = new StoreModel();
                        DialogUIHelper.createStoreDialog(model, StoreListActivity.this);
                        return true;
                    }
                });
                break;
            case R.id.log_out:
                mDBHelper.signOut(this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onItemClick(int position) {
//
//    }
}
