package com.example.misteryshopper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.ShopperModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ShopperProfileUiState {
    object Loading : ShopperProfileUiState()
    data class Success(val shopper: ShopperModel) : ShopperProfileUiState()
    data class Error(val message: String) : ShopperProfileUiState()
}

class ShopperProfileViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _uiState = MutableStateFlow<ShopperProfileUiState>(ShopperProfileUiState.Loading)
    val uiState: StateFlow<ShopperProfileUiState> = _uiState

    fun loadShopper(email: String?) {
        if (email == null) {
            _uiState.value = ShopperProfileUiState.Error("Email not provided")
            return
        }

        viewModelScope.launch {
            _uiState.value = ShopperProfileUiState.Loading
            try {
                val shopper = dbHelper.getShopperByMail(email)
                if (shopper != null) {
                    _uiState.value = ShopperProfileUiState.Success(shopper)
                } else {
                    _uiState.value = ShopperProfileUiState.Error("Shopper not found")
                }
            } catch (e: Exception) {
                _uiState.value = ShopperProfileUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

class ShopperProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopperProfileViewModel::class.java)) {
            return ShopperProfileViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
