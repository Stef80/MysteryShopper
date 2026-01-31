package com.example.misteryshopper.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.utils.ConfigurableList
import com.example.misteryshopper.viewmodels.ShopperListUiState
import com.example.misteryshopper.viewmodels.ShopperListViewModel
import com.example.misteryshopper.viewmodels.ShopperListViewModelFactory

class ShopperListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("store", StoreModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("store") as? StoreModel
        }

        setContent {
            ShopperListScreen(storeForHiring = store)
        }
    }
}

@Composable
fun ShopperListScreen(
    storeForHiring: StoreModel?,
    viewModel: ShopperListViewModel = viewModel(factory = ShopperListViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shoppers") },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, StoreListActivity::class.java))
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        ShopperListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            storeForHiring = storeForHiring
        )
    }
}

@Composable
fun ShopperListContent(
    modifier: Modifier = Modifier,
    uiState: ShopperListUiState,
    storeForHiring: StoreModel?
) {
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
                    ConfigurableList(
                        items = uiState.shoppers,
                        storeForHiring = storeForHiring,
                        onItemClick = { item ->
                            if (item is ShopperModel) {
                                val go = Intent(context, ShopperProfileActivity::class.java)
                                go.putExtra("email", item.email)
                                context.startActivity(go)
                            }
                        }
                    )
                }
            }
        }
    }
}

// Previews
@Preview(showBackground = true, name = "Shopper List With Data")
@Composable
fun ShopperListPreview() {
    val shoppers = listOf(
        ShopperModel(name = "John", surname = "Doe").apply { email = "john.d@example.com" },
        ShopperModel(name = "Jane", surname = "Smith").apply { email = "jane.s@example.com" }
    )
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Success(shoppers), storeForHiring = null)
    }
}

@Preview(showBackground = true, name = "Shopper List Empty")
@Composable
fun ShopperListEmptyPreview() {
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Success(emptyList()), storeForHiring = null)
    }
}

@Preview(showBackground = true, name = "Shopper List Error")
@Composable
fun ShopperListErrorPreview() {
    MaterialTheme {
        ShopperListContent(uiState = ShopperListUiState.Error("Failed to load shoppers"), storeForHiring = null)
    }
}
