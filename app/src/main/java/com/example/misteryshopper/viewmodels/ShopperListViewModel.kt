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

sealed class ShopperListUiState {
    object Loading : ShopperListUiState()
    data class Success(val shoppers: List<ShopperModel>) : ShopperListUiState()
    data class Error(val message: String) : ShopperListUiState()
}

class ShopperListViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _uiState = MutableStateFlow<ShopperListUiState>(ShopperListUiState.Loading)
    val uiState: StateFlow<ShopperListUiState> = _uiState

    init {
        loadShoppers()
    }

    fun loadShoppers() {
        viewModelScope.launch {
            _uiState.value = ShopperListUiState.Loading
            try {
                val shoppers = dbHelper.readShoppers()
                _uiState.value = ShopperListUiState.Success(shoppers)
            } catch (e: Exception) {
                _uiState.value = ShopperListUiState.Error(e.message ?: "Failed to load shoppers")
            }
        }
    }
}

class ShopperListViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopperListViewModel::class.java)) {
            return ShopperListViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
