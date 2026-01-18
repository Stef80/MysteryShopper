package com.example.misteryshopper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.activity.RegisterEmployerActivity
import com.example.misteryshopper.activity.RegisterShopperActivity
import com.example.misteryshopper.activity.ShopperProfileActivity
import com.example.misteryshopper.activity.StoreListActivity
import com.example.misteryshopper.models.EmployerModel
import com.example.misteryshopper.viewmodels.AuthState
import com.example.misteryshopper.viewmodels.MainViewModel
import com.example.misteryshopper.viewmodels.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel(factory = MainViewModelFactory())) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val user = (authState as AuthState.Authenticated).user
            val intent = if (user is EmployerModel) {
                Intent(context, StoreListActivity::class.java)
            } else {
                Intent(context, ShopperProfileActivity::class.java).apply {
                    putExtra("email", user.email)
                }
            }
            context.startActivity(intent)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (authState) {
            is AuthState.Loading -> {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AuthState.Unauthenticated, is AuthState.Error -> {
                LoginScreen(
                    error = (authState as? AuthState.Error)?.message,
                    onLoginClick = { email, password ->
                        viewModel.login(email, password, context)
                    }
                )
            }
            is AuthState.Authenticated -> {
                // Handled by LaunchedEffect, shows a loading indicator while navigating
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun LoginScreen(error: String?, onLoginClick: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showRegisterDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.login),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.prompt_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = android.R.string.ok))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.action_sign_in),
            modifier = Modifier.clickable { showRegisterDialog = true },
            color = MaterialTheme.colors.primary,
            fontSize = 18.sp
        )
    }

    if (showRegisterDialog) {
        RegistrationChoiceDialog(
            onDismiss = { showRegisterDialog = false },
            onShopperClick = {
                context.startActivity(Intent(context, RegisterShopperActivity::class.java))
                showRegisterDialog = false
            },
            onEmployerClick = {
                context.startActivity(Intent(context, RegisterEmployerActivity::class.java))
                showRegisterDialog = false
            }
        )
    }
}

@Composable
fun RegistrationChoiceDialog(
    onDismiss: () -> Unit,
    onShopperClick: () -> Unit,
    onEmployerClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register As") },
        text = { Text("Are you a Shopper or an Employer?") },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onShopperClick) { Text("Shopper") }
                Button(onClick = onEmployerClick) { Text("Employer") }
            }
        }
    )
}
