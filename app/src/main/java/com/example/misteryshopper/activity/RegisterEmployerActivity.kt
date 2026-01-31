package com.example.misteryshopper.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.models.EmployerModel
import com.example.misteryshopper.viewmodels.RegisterEmployerViewModel
import com.example.misteryshopper.viewmodels.RegisterEmployerViewModelFactory
import com.example.misteryshopper.viewmodels.RegistrationState

class RegisterEmployerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterEmployerScreen()
        }
    }
}

@Composable
fun RegisterEmployerScreen(viewModel: RegisterEmployerViewModel = viewModel(factory = RegisterEmployerViewModelFactory())) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var pIva by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val registrationState by viewModel.registrationState.collectAsState()

    // Handle side-effects like navigation or toasts
    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is RegistrationState.Success -> {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            is RegistrationState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState() // Reset state after showing error
            }
            else -> Unit // Idle or Loading
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text(stringResource(R.string.category)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pIva, onValueChange = { pIva = it }, label = { Text(stringResource(R.string.iva)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.prompt_email)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.prompt_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val employer = EmployerModel(
                    emName = name,
                    category = category,
                    pIva = pIva
                ).apply { this.email = email }
                viewModel.registerEmployer(employer, email, password, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            enabled = registrationState !is RegistrationState.Loading
        ) {
            if (registrationState is RegistrationState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(R.string.action_sign_in_short),
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RegisterEmployerScreen()
}
