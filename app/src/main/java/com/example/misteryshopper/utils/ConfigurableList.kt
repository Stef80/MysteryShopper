package com.example.misteryshopper.utils

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.misteryshopper.activity.ShopperListActivity
import com.example.misteryshopper.models.HiringModel
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.viewmodels.HiringListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConfigurableList(
    items: List<Any>,
    hiringViewModel: HiringListViewModel? = null, // Opzionale, solo per la lista di ingaggi
    onItemClick: (Any) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            when (item) {
                is ShopperModel -> ShopperListItem(shopper = item, onClick = { onItemClick(item) })
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
fun ShopperListItem(shopper: ShopperModel, onClick: () -> Unit) {
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
            Button(onClick = {
                // La logica di "assunzione" dovrebbe essere gestita nel ViewModel o passata come lambda
            }) {
                Text("Hire")
            }
        }
    }
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
