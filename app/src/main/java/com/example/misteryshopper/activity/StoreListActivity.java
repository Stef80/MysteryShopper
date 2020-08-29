package com.example.misteryshopper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import static android.content.ContentValues.TAG;

import java.util.List;

public class StoreListActivity extends AppCompatActivity implements RecyclerViewConfig.OnItemClickListener{


    private DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<StoreModel> storeModelList;
    private TextView textEmpty;
    String userID;


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
                        storeModelList =(List<StoreModel>) obj;
                        new RecyclerViewConfig(null).setConfigList(recyclerView, StoreListActivity.this, storeModelList,
                                keys, StoreListActivity.this);
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
          String idStore = new SharedPrefConfig(getApplicationContext()).readPrefString("store_id");
            PictureHandler.uploaImageWithoutShow(idStore,"Store",getApplicationContext());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        menu.removeItem(R.id.item_back);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add:
                        StoreModel model = new StoreModel();
                        DialogUIHelper.createStoreDialog(model, StoreListActivity.this);
                        return true;
            case R.id.log_out:
                mDBHelper.signOut(getApplicationContext());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
               return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {

    }

}
