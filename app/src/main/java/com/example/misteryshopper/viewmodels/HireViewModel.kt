package com.example.misteryshopper.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.R
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.HiringModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.utils.notification.MessageCreationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HireState {
    object Idle : HireState()
    object Loading : HireState()
    object Success : HireState()
    data class Error(val message: String) : HireState()
}

class HireViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _hireState = MutableStateFlow<HireState>(HireState.Idle)
    val hireState: StateFlow<HireState> = _hireState

    fun hireShopper(
        context: Context,
        store: StoreModel,
        shopperMail: String,
        date: String,
        fee: Double,
        employerId: String
    ) {
        viewModelScope.launch {
            _hireState.value = HireState.Loading
            try {
                val now = System.currentTimeMillis().toString()
                val hiringModel = HiringModel(
                    id = now + employerId,
                    idEmployer = employerId,
                    employerName = store.eName,
                    mailShopper = shopperMail,
                    address = "${store.city}, ${store.address}",
                    idStore = store.idStore,
                    date = date,
                    fee = fee
                )

                dbHelper.addHiringModel(hiringModel)

                // Send notification
                val token = dbHelper.getTokenByMail(shopperMail)
                if (token != null) {
                    MessageCreationService.buildMessage(
                        context, token,
                        context.getString(R.string.notification_of_employment),
                        "${store.city}\n${store.address}",
                        date,
                        fee.toString(),
                        store.eName,
                        employerId,
                        hiringModel.id,
                        store.imageUri
                    )
                }
                _hireState.value = HireState.Success
            } catch (e: Exception) {
                _hireState.value = HireState.Error(e.message ?: "Failed to hire shopper.")
            }
        }
    }

    fun resetState() {
        _hireState.value = HireState.Idle
    }
}

class HireViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HireViewModel::class.java)) {
            return HireViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
