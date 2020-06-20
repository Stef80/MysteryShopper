package com.example.misteryshopper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.utils.DBHelper;
import com.example.misteryshopper.utils.FirebaseDBHelper;

import java.util.List;


public class RegisterShopperActivity extends AppCompatActivity {

   private EditText name;
   private EditText surname;
   private EditText address;
   private EditText city;
   private EditText cf;
   private EditText email;
   private EditText password;
   private DBHelper mDbHelper = FirebaseDBHelper.getInstance()
           ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.nameEditText);

        surname = findViewById(R.id.surnameEditTxt);

        cf = findViewById(R.id.cfEditTxt);

        address = findViewById(R.id.addressEditTxt);

        city = findViewById(R.id.cityEditTxt);

        email = findViewById(R.id.emailEditRegTxt);

        password = findViewById(R.id.passwordEditRegTxt);

        Button rButton = findViewById(R.id.buttonRegister);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopperModel shopper = new ShopperModel();
                shopper.setName(name.getText().toString());
                shopper.setSurname(surname.getText().toString());
                shopper.setAddress(address.getText().toString());
                shopper.setCf(cf.getText().toString());
                shopper.setCity(city.getText().toString());
                String mail = email.getText().toString();
                String pas = password.getText().toString();
                if(TextUtils.isEmpty(mail)){
                    email.setError(getResources().getString(R.string.email_not_inserted));
                }
                if(TextUtils.isEmpty(pas)){
                    password.setError(getResources().getString(R.string.invalid_password));
                }
                if(pas.length()<=5){
                    password.setError(getResources().getString(R.string.invalid_password_lenght));
                }
                shopper.setEmail(email.getText().toString());
                 if(!TextUtils.isEmpty(mail)&& !TextUtils.isEmpty(pas))
                mDbHelper.register(shopper, mail,pas, getApplicationContext(), new FirebaseDBHelper.DataStatus() {
                    @Override
                    public void dataIsLoaded(List<?> obj, List<String> keys) {
                        startActivity(new Intent(RegisterShopperActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });

    }
}
