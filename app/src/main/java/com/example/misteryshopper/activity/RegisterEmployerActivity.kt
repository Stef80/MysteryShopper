package com.example.misteryshopper.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.EmployerModel

class RegisterEmployerActivity : ComponentActivity() {

    private val mDbHepler: DBHelper = FirebaseDBHelper.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterEmployerScreen(onRegisterClick = { employer, email, password ->
                registerEmployer(employer, email, password)
            })
        }
    }

    private fun registerEmployer(employer: EmployerModel, email: String, password: String) {
        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            Toast.makeText(this, getString(R.string.email_not_inserted), Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.prompt_password), Toast.LENGTH_SHORT).show()
            return
        }

        mDbHepler.register(employer, email, password, applicationContext, object : FirebaseDBHelper.DataStatus {
            override fun dataIsLoaded(obj: List<*>?, keys: List<String>?) {
                Toast.makeText(this@RegisterEmployerActivity, "inserted succeffull", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegisterEmployerActivity, MainActivity::class.java))
                finish()
            }
        })
    }
}

@Composable
fun RegisterEmployerScreen(onRegisterClick: (EmployerModel, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var pIva by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text(stringResource(R.string.category)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pIva,
            onValueChange = { pIva = it },
            label = { Text(stringResource(R.string.iva)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.prompt_email)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.prompt_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val employer = EmployerModel().apply {
                    emName = name
                    this.category = category
                    this.pIva = pIva
                    this.email = email
                }
                onRegisterClick(employer, email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.action_sign_in_short),
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RegisterEmployerScreen { _, _, _ -> }
}
