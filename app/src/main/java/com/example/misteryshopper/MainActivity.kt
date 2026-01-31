package com.example.misteryshopper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

private const val TAG = "MainActivity"
private const val ERROR_DIALOG_REQUEST = 9001

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                onCheckServices = {
                    isServicesOk()
                }
            )
        }
    }

    private fun isServicesOk(): Boolean {
        Log.d(TAG, "isServicesOk: checking google services version")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(TAG, "isServicesOk: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                Log.d(TAG, "isServicesOk: an error occurred but we can fix it")
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
                dialog?.show()
            }
            else -> {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(factory = MainViewModelFactory()),
    onCheckServices: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onCheckServices()
    }

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

@Preview(showBackground = true, name = "Login Screen Preview")
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(error = null, onLoginClick = { _, _ -> })
    }
}
