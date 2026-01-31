package com.example.misteryshopper.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.EmployerModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.utils.SharedPrefConfig
import com.example.misteryshopper.viewmodels.StoreUiState
import com.example.misteryshopper.viewmodels.StoreViewModel
import com.example.misteryshopper.viewmodels.StoreViewModelFactory
import java.io.File

class StoreListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userId = SharedPrefConfig(applicationContext).readLoggedUser()?.id
            val notificationName = intent.getStringExtra("notification_name")
            val notificationOutcome = intent.getStringExtra("notification_outcome")

            StoreListScreen(
                userId = userId,
                notificationName = notificationName,
                notificationOutcome = notificationOutcome
            )
        }
    }
}

@Composable
fun StoreListScreen(
    userId: String?,
    notificationName: String?,
    notificationOutcome: String?,
    viewModel: StoreViewModel = viewModel(factory = StoreViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddStoreDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(notificationName != null) }


    LaunchedEffect(key1 = userId) {
        if (userId != null) {
            viewModel.loadStores(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Stores") },
                actions = {
                    IconButton(onClick = {
                        FirebaseDBHelper.instance.signOut(context)
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddStoreDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Store")
            }
        }
    ) { paddingValues ->
        StoreListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState
        )

        if (showAddStoreDialog) {
            AddStoreDialog(
                viewModel = viewModel,
                onDismiss = { showAddStoreDialog = false }
            )
        }

        if (showNotificationDialog) {
            NotificationResponseDialog(
                name = notificationName!!,
                outcome = notificationOutcome!!,
                onDismiss = { showNotificationDialog = false }
            )
        }
    }
}

@Composable
fun StoreListContent(modifier: Modifier = Modifier, uiState: StoreUiState) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is StoreUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is StoreUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colors.error)
                }
            }
            is StoreUiState.Success -> {
                if (uiState.stores.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.no_item_to_show), style = MaterialTheme.typography.h6)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stores) { store ->
                            StoreItem(store = store) {
                                // Handle store item click if needed
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StoreItem(store: StoreModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = store.eName ?: "Store", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(4.dp))
            Text(text = "ID: ${store.idStore}")
            Text(text = "Manager: ${store.manager}")
            Text(text = "${store.city}, ${store.address}")
        }
    }
}

@Composable
fun AddStoreDialog(viewModel: StoreViewModel, onDismiss: () -> Unit) {
    var idStore by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val loggedUser = SharedPrefConfig(context).readLoggedUser()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // The URI is already available in the variable passed to launch()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Store") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = idStore, onValueChange = { idStore = it }, label = { Text("Store ID") })
                TextField(value = manager, onValueChange = { manager = it }, label = { Text("Manager Name") })
                TextField(value = city, onValueChange = { city = it }, label = { Text("City") })
                TextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                Button(onClick = {
                    val file = File(context.cacheDir, "temp_image.jpg")
                    val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    imageUri = uri
                    imagePickerLauncher.launch(uri)
                }) {
                    Text(if (imageUri == null) "Add Image" else "Change Image")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newStore = StoreModel(
                    idStore = idStore,
                    manager = manager,
                    city = city,
                    address = address,
                    idEmployer = loggedUser?.id,
                    eName = (loggedUser as? EmployerModel)?.emName ?: "Unknown"
                )
                viewModel.addStore(newStore)
                imageUri?.let { uri ->
                    viewModel.addImageToStore(idStore, uri, context)
                }
                onDismiss()
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun NotificationResponseDialog(name: String, outcome: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hiring Response") },
        text = { Text("$name has $outcome your request.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true, name = "Store List With Data")
@Composable
fun StoreListPreview() {
    val stores = listOf(
        StoreModel(eName = "Store 1", idStore = "123", manager = "Mr. Smith", city = "New York", address = "1st Ave"),
        StoreModel(eName = "Store 2", idStore = "456", manager = "Ms. Jones", city = "London", address = "Baker St")
    )
    MaterialTheme {
        StoreListContent(uiState = StoreUiState.Success(stores))
    }
}

@Preview(showBackground = true, name = "Store List Empty")
@Composable
fun StoreListEmptyPreview() {
    MaterialTheme {
        StoreListContent(uiState = StoreUiState.Success(emptyList()))
    }
}

@Preview(showBackground = true, name = "Add Store Dialog")
@Composable
fun AddStoreDialogPreview() {
    MaterialTheme {
        AddStoreDialog(viewModel = StoreViewModel(FirebaseDBHelper.instance), onDismiss = {})
    }
}
