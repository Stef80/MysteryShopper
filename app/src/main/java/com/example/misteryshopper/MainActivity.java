package com.example.misteryshopper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.misteryshopper.activity.ShopperProfileActivity;
import com.example.misteryshopper.activity.StoreListActivity;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.models.User;
import com.example.misteryshopper.utils.DialogUIHelper;
import com.example.misteryshopper.utils.SharedPrefConfig;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String SHOPPER = "Shopper";
    private static final String EMPLOYER = "Employer";
    private EditText userName;
    private EditText password;
    SharedPrefConfig sharedPrefConfig;

    private DBHelper mDbHelper = FirebaseDBHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefConfig = new SharedPrefConfig(getApplicationContext());
        if (sharedPrefConfig.readLoggedUser() != null) {
            User user = sharedPrefConfig.readLoggedUser();
            String role = user.getRole();
            goByRole(role);
        }

        userName = findViewById(R.id.eMailTxt);
        password = findViewById(R.id.pwdTxt);

        Button ok = findViewById(R.id.enter);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userName.getText().toString();
                String passwordStr = password.getText().toString();
                if (TextUtils.isEmpty(user) && !user.contains("@")) {
                    userName.setError(getResources().getString(R.string.email_not_inserted));
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    password.setError(getString(R.string.invalid_password));
                }

                    mDbHelper.login(user, passwordStr, MainActivity.this, new DBHelper.DataStatus() {
                        @Override
                        public void dataIsLoaded(List<?> obj, List<String> keys) {
                            mDbHelper.addTokenToUser((User) obj.get(0), MainActivity.this);
                            sharedPrefConfig.writeLoggedUser((User) obj.get(0));
                            goByRole(((User)obj.get(0)).getRole());
                        }
                    });
            }
        });

        TextView register = findViewById(R.id.registerText);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUIHelper.createRegistationDialog(MainActivity.this);
            }
        });

    }

    private void goByRole(String role) {
        if (role.equals(SHOPPER)) {
            Intent intent = new Intent(MainActivity.this, ShopperProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email", sharedPrefConfig.readLoggedUser().getEmail());
            startActivity(intent);
        } else if (role.equals(EMPLOYER)) {
            Intent intent =new Intent(MainActivity.this, StoreListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}