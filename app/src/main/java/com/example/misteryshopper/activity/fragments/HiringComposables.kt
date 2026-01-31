package com.example.misteryshopper.activity.fragments

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.MapsActivity
import com.example.misteryshopper.models.HiringModel
import com.example.misteryshopper.viewmodels.HiringListViewModel
import com.example.misteryshopper.viewmodels.HiringListViewModelFactory
import com.example.misteryshopper.viewmodels.HiringUiState
import java.text.SimpleDateFormat
import java.util.*


private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@Composable
fun ListHiringScreen(
    viewModel: HiringListViewModel = viewModel(factory = HiringListViewModelFactory()),
    userEmail: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = userEmail) {
        viewModel.loadHirings(userEmail)
    }

    when (val state = uiState) {
        is HiringUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HiringUiState.Success -> {
            if (state.hirings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.no_item_to_show),
                        style = MaterialTheme.typography.h6
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.hirings) { hiring ->
                        HiringItem(hiring = hiring, viewModel = viewModel, onClick = {
                            val intent = Intent(context, MapsActivity::class.java)
                            intent.putExtra("address", hiring.address)
                            context.startActivity(intent)
                        })
                    }
                }
            }
        }
        is HiringUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
fun HiringItem(hiring: HiringModel, viewModel: HiringListViewModel, onClick: () -> Unit) {
    val isPast = isDatePast(hiring.date)
    val isActionable = hiring.accepted.isNullOrEmpty() && !isPast

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Employer: ${hiring.employerName}", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Store ID: ${hiring.idStore}")
            Text(text = "Address: ${hiring.address}")
            Text(text = "Date: ${hiring.date}")
            Text(text = "Fee: ${hiring.fee} €")
            Spacer(modifier = Modifier.height(8.dp))

            val statusText: String
            val statusColor: Color
            when {
                isPast -> {
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
                    Button(onClick = { viewModel.setOutcome(hiring.id!!, "accepted") }) {
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
        val hireDate = dateFormat.parse(dateStr)
        val now = Date()
        hireDate?.before(now) ?: false
    } catch (e: Exception) {
        false
    }
}
