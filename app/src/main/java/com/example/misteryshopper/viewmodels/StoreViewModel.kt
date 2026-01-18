package com.example.misteryshopper.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.StoreModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class StoreUiState {
    object Loading : StoreUiState()
    data class Success(val stores: List<StoreModel>) : StoreUiState()
    data class Error(val message: String) : StoreUiState()
}

class StoreViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState

    private var currentUserId: String? = null

    fun loadStores(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading
            try {
                val stores = dbHelper.readStoreOfSpecificUser(userId)
                _uiState.value = StoreUiState.Success(stores)
            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error(e.message ?: "Failed to load stores")
            }
        }
    }

    fun addStore(model: StoreModel) {
        viewModelScope.launch {
            try {
                dbHelper.addStoreOfSpecificId(model)
                // Reload the list to show the new store
                currentUserId?.let { loadStores(it) }
            } catch (e: Exception) {
                // We can expose a specific error for this operation if needed
                _uiState.value = StoreUiState.Error(e.message ?: "Failed to add store")
            }
        }
    }

    fun addImageToStore(id: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                dbHelper.addImageToStoreById(id, imageUri, context)
                // Reload the list to show the updated image (if the URI is stored in the model)
                currentUserId?.let { loadStores(it) }
            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error(e.message ?: "Failed to upload image")
            }
        }
    }
}

class StoreViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            return StoreViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
