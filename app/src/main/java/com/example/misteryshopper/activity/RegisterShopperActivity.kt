package com.example.misteryshopper.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.DBHelper.DataStatus
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.utils.camera.PictureHandler

class RegisterShopperActivity : ComponentActivity() {
    private val mDbHelper: DBHelper = FirebaseDBHelper.instance
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterShopperScreen(
                onRegisterClick = { shopper, email, pass ->
                    registerShopper(shopper, email, pass)
                }
            )
        }
    }

    private fun registerShopper(shopper: ShopperModel, mail: String, pas: String) {
        var error = false
        if (TextUtils.isEmpty(mail)) {
            Toast.makeText(this, R.string.email_not_inserted, Toast.LENGTH_SHORT).show()
            error = true
        }
        if (TextUtils.isEmpty(pas)) {
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show()
            error = true
        }
        if (pas.length <= 5) {
            Toast.makeText(this, R.string.invalid_password_lenght, Toast.LENGTH_SHORT).show()
            error = true
        }
        if (error) return

        mDbHelper.register(
            shopper,
            mail,
            pas,
            applicationContext,
            object : DataStatus {
                override fun dataIsLoaded(
                    obj: MutableList<*>?,
                    keys: MutableList<String?>?
                ) {
                    userId = mDbHelper.idCurrentUser
                    PictureHandler.uploaImageWithoutShow(
                        userId,
                        "Shopper",
                        applicationContext
                    )
                    startActivity(
                        Intent(
                            this@RegisterShopperActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            })
    }
}


@Composable
fun RegisterShopperScreen(onRegisterClick: (ShopperModel, String, String) -> Unit) {

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var cf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Picture was taken successfully
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text(stringResource(R.string.surname)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.address)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text(stringResource(R.string.city)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = cf, onValueChange = { cf = it }, label = { Text(stringResource(R.string.cf)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.prompt_email)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.prompt_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val intent = PictureHandler.getIntentForPicture(context)
            takePictureLauncher.launch(intent)
        }) {
            Text(text = stringResource(id = R.string.add_image))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val shopper = ShopperModel().apply {
                    this.name = name
                    this.surname = surname
                    this.address = address
                    this.city = city
                    this.cf = cf
                    this.email = email
                }
                onRegisterClick(shopper, email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.action_sign_in_short))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultRegisterShopperPreview() {
    RegisterShopperScreen { _, _, _ -> }
}
