package com.example.misteryshopper.utils

import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.misteryshopper.activity.ShopperProfileActivity
import com.example.misteryshopper.models.HiringModel
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.viewmodels.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.misteryshopper.activity.ShopperListActivity

@Composable
fun ConfigurableList(
    items: List<Any>,
    hiringViewModel: HiringListViewModel? = null, // Optional, only for hirings list
    storeForHiring: StoreModel? = null, // Optional, needed to initiate hiring
    onItemClick: (Any) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            when (item) {
                is ShopperModel -> ShopperListItem(
                    shopper = item,
                    store = storeForHiring,
                    onClick = { onItemClick(item) }
                )
                is StoreModel -> StoreListItem(store = item, onClick = { onItemClick(item) })
                is HiringModel -> {
                    if (hiringViewModel != null) {
                        HiringListItem(hiring = item, viewModel = hiringViewModel, onClick = { onItemClick(item) })
                    }
                }
            }
        }
    }
}

@Composable
fun ShopperListItem(shopper: ShopperModel, store: StoreModel?, onClick: () -> Unit) {
    var showHireDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            AsyncImage(
                model = shopper.imageUri,
                contentDescription = "Shopper Profile Image",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${shopper.name} ${shopper.surname}", style = MaterialTheme.typography.h6)
                Text(text = shopper.city ?: "N/A")
            }
            // Show hire button only if a store context is provided
            store?.let {
                Button(onClick = { showHireDialog = true }) {
                    Text("Hire")
                }
            }
        }
    }

    if (showHireDialog && store != null && shopper.email != null) {
        HireShopperDialog(
            store = store,
            shopperMail = shopper.email!!,
            onDismiss = { showHireDialog = false }
        )
    }
}

@Composable
fun HireShopperDialog(
    store: StoreModel,
    shopperMail: String,
    onDismiss: () -> Unit,
    viewModel: HireViewModel = viewModel(factory = HireViewModelFactory())
) {
    var fee by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current
    val hireState by viewModel.hireState.collectAsState()

    LaunchedEffect(hireState) {
        if (hireState is HireState.Success) {
            Toast.makeText(context, "Hiring request sent!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onDismiss()
        } else if (hireState is HireState.Error) {
            Toast.makeText(context, (hireState as HireState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hire ${shopperMail}") },
        text = {
            Column {
                Text("Store: ${store.eName} at ${store.address}")
                Spacer(modifier = Modifier.height(16.dp))
                // Basic Date Picker using AndroidView
                AndroidView(
                    factory = { DatePicker(it).apply { minDate = System.currentTimeMillis() } },
                    update = {
                        it.init(
                            selectedDate.get(Calendar.YEAR),
                            selectedDate.get(Calendar.MONTH),
                            selectedDate.get(Calendar.DAY_OF_MONTH)
                        ) { _, year, month, day ->
                            selectedDate = Calendar.getInstance().apply { set(year, month, day) }
                        }
                    }
                )
                OutlinedTextField(value = fee, onValueChange = { fee = it }, label = { Text("Fee (€)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                    viewModel.hireShopper(
                        context = context,
                        store = store,
                        shopperMail = shopperMail,
                        date = formattedDate,
                        fee = fee.toDoubleOrNull() ?: 0.0,
                        employerId = store.idEmployer ?: ""
                    )
                },
                enabled = hireState !is HireState.Loading
            ) {
                if (hireState is HireState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Confirm")
                }
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}


@Composable
fun StoreListItem(store: StoreModel, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = store.eName ?: "Store", style = MaterialTheme.typography.h6)
                Text(text = "ID: ${store.idStore}", fontWeight = FontWeight.Bold)
                Text(text = "${store.city}, ${store.address}")
                Text(text = "Manager: ${store.manager}")
            }
            Button(onClick = {
                val search = Intent(context, ShopperListActivity::class.java)
                search.putExtra("store", store)
                context.startActivity(search)
            }) {
                Text("Search")
            }
        }
    }
}

// HiringListItem remains the same
@Composable
fun HiringListItem(hiring: HiringModel, viewModel: HiringListViewModel, onClick: () -> Unit) {
    val context = LocalContext.current
    val isActionable = hiring.accepted.isNullOrEmpty() && !isDatePast(hiring.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Employer: ${hiring.employerName}", style = MaterialTheme.typography.h6)
            Text(text = "Store ID: ${hiring.idStore}")
            Text(text = "Address: ${hiring.address}")
            Text(text = "Date: ${hiring.date}")
            Text(text = "Fee: ${hiring.fee} €")

            Spacer(modifier = Modifier.height(8.dp))

            val statusText: String
            val statusColor: Color
            when {
                isDatePast(hiring.date) -> {
                    statusText = "Done"
                    statusColor = Color.Gray
                }
                hiring.accepted == "accepted" -> {
                    statusText = "Accepted"
                    statusColor = Color.Green
                }
                hiring.accepted == "declined" -> {
                    statusText = "Declined"
                    statusColor = Color.Red
                }
                else -> {
                    statusText = "Waiting for response"
                    statusColor = Color.Blue
                }
            }
            Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold)

            if (isActionable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { viewModel.setOutcome(hiring.id!!, "declined") },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) {
                        Text("Decline")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.setOutcome(hiring.id!!, "accepted") }
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

private fun isDatePast(dateStr: String?): Boolean {
    if (dateStr == null) return false
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hireDate = format.parse(dateStr)
        val now = Date()
        hireDate?.before(now) ?: false
    } catch (e: Exception) {
        false
    }
}
