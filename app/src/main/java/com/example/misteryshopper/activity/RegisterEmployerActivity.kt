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
import com.example.misteryshopper.models.EmployerModel;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;

import java.util.List;

public class RegisterEmployerActivity extends AppCompatActivity {

    private EditText emNameTxt;
    private EditText categoryTxt;
    private EditText pIva;
    private EditText email;
    private EditText password;
    private Button regButton;
    private DBHelper mDbHepler = FirebaseDBHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employer);

        emNameTxt = findViewById(R.id.nameEmployerEditText);
        categoryTxt = findViewById(R.id.categoryEditTxt);
        pIva = findViewById(R.id.pIvaEditTxt);
        regButton = findViewById(R.id.emRegButton);
        email = findViewById(R.id.emEmailEditRegTxt);
        password = findViewById(R.id.emPasswordEditTxt);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmployerModel model = new EmployerModel();
                model.setEmName(emNameTxt.getText().toString());
                model.setCategory(categoryTxt.getText().toString());
                model.setpIva(pIva.getText().toString());
                String mail = email.getText().toString();
                String pas = password.getText().toString();

                if(TextUtils.isEmpty(mail) || ! mail.contains("@")){
                     email.setError(getString(R.string.email_not_inserted));
                }

                if(TextUtils.isEmpty(pas)){
                    email.setError(getString(R.string.prompt_password));
                }
                model.setEmail(mail);
                mDbHepler.register(model, mail, pas,getApplicationContext(), new FirebaseDBHelper.DataStatus() {
                    @Override
                    public void dataIsLoaded(List<?> obj, List<String> keys) {
                        Toast.makeText(RegisterEmployerActivity.this,"inserted succeffull",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterEmployerActivity.this, MainActivity.class));
                    }
                });
            }
        });
    }
}
