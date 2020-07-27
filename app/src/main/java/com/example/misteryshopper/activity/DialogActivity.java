package com.example.misteryshopper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.misteryshopper.utils.DialogUIHelper;

public class DialogActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent notificationIntent = getIntent();
        Bundle extras = notificationIntent.getExtras();
        String name = extras.getString("name");
        String outcome = extras.getString("outcome");
        DialogUIHelper.buildDialogResponse(DialogActivity.this,name,outcome);
    }
}
