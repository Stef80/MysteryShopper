package com.example.misteryshopper.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.viewmodels.RegisterShopperViewModel
import com.example.misteryshopper.viewmodels.RegisterShopperViewModelFactory
import com.example.misteryshopper.viewmodels.RegistrationState
import java.io.File

class RegisterShopperActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterShopperScreen()
        }
    }
}

@Composable
fun RegisterShopperScreen(viewModel: RegisterShopperViewModel = viewModel(factory = RegisterShopperViewModelFactory())) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var cf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val registrationState by viewModel.registrationState.collectAsState()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // imageUri is already updated
        }
    }

    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is RegistrationState.Success -> {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            is RegistrationState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
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
            val file = File(context.cacheDir, "temp_shopper_image.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            imageUri = uri
            takePictureLauncher.launch(uri)
        }) {
            Text(text = if (imageUri == null) stringResource(id = R.string.add_image) else "Retake Image")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val shopper = ShopperModel(
                    name = name,
                    surname = surname,
                    address = address,
                    city = city,
                    cf = cf
                ).apply { this.email = email }
                viewModel.registerShopper(shopper, email, password, imageUri, context)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = registrationState !is RegistrationState.Loading
        ) {
            if (registrationState is RegistrationState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(id = R.string.action_sign_in_short))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultRegisterShopperPreview() {
    RegisterShopperScreen()
}
