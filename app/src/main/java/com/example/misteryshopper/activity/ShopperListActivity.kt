package com.example.misteryshopper.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.R
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.viewmodels.ShopperListUiState
import com.example.misteryshopper.viewmodels.ShopperListViewModel
import com.example.misteryshopper.viewmodels.ShopperListViewModelFactory

class ShopperListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShopperListScreen()
        }
    }
}

@Composable
fun ShopperListScreen(viewModel: ShopperListViewModel = viewModel(factory = ShopperListViewModelFactory())) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shoppers") },
                navigationIcon = {
                    IconButton(onClick = {
                        val backIntent = Intent(context, StoreListActivity::class.java)
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(backIntent)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        ShopperListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState
        )
    }
}

@Composable
fun ShopperListContent(modifier: Modifier = Modifier, uiState: ShopperListUiState) {
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is ShopperListUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ShopperListUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colors.error)
                }
            }
            is ShopperListUiState.Success -> {
                if (uiState.shoppers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.no_item_to_show), style = MaterialTheme.typography.h6)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.shoppers) { shopper ->
                            ShopperItem(shopper = shopper) {
                                val go = Intent(context, ShopperProfileActivity::class.java)
                                go.putExtra("email", shopper.email)
                                context.startActivity(go)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ShopperItem(shopper: ShopperModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Qui potresti aggiungere un'immagine del profilo se disponibile
            // AsyncImage(model = shopper.imageUri, contentDescription = "Shopper image", modifier = Modifier.size(50.dp))
            // Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${shopper.name} ${shopper.surname}", style = MaterialTheme.typography.h6)
                Text(text = shopper.email ?: "", style = MaterialTheme.typography.body2)
            }
        }
    }
}


@Preview(showBackground = true, name = "Shopper List With Data")
@Composable
fun ShopperListPreview() {
    val shoppers = listOf(
        ShopperModel(name = "John", surname = "Doe", email = "john.d@example.com"),
        ShopperModel(name = "Jane", surname = "Smith", email = "jane.s@example.com")
    )
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Success(shoppers))
    }
}

@Preview(showBackground = true, name = "Shopper List Empty")
@Composable
fun ShopperListEmptyPreview() {
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Success(emptyList()))
    }
}

@Preview(showBackground = true, name = "Shopper List Error")
@Composable
fun ShopperListErrorPreview() {
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Error("Failed to load shoppers"))
    }
}
