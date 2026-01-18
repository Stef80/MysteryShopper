package com.example.misteryshopper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.HiringModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Definiamo gli stati della UI per chiarezza
sealed class HiringUiState {
    object Loading : HiringUiState()
    data class Success(val hirings: List<HiringModel>) : HiringUiState()
    data class Error(val message: String) : HiringUiState()
}

class HiringListViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _uiState = MutableStateFlow<HiringUiState>(HiringUiState.Loading)
    val uiState: StateFlow<HiringUiState> = _uiState

    // Manteniamo l'email corrente per poter ricaricare i dati facilmente
    private var currentUserEmail: String? = null

    fun loadHirings(mail: String) {
        currentUserEmail = mail
        viewModelScope.launch {
            _uiState.value = HiringUiState.Loading
            try {
                val hirings = dbHelper.getHireByMail(mail).sorted()
                _uiState.value = HiringUiState.Success(hirings)
            } catch (e: Exception) {
                _uiState.value = HiringUiState.Error(e.message ?: "Failed to load hirings")
            }
        }
    }

    fun addHiring(model: HiringModel) {
        viewModelScope.launch {
            try {
                dbHelper.addHiringModel(model)
                // Ricarica la lista per mostrare il nuovo elemento
                currentUserEmail?.let { loadHirings(it) }
            } catch (e: Exception) {
                _uiState.value = HiringUiState.Error(e.message ?: "Failed to add hiring")
            }
        }
    }

    fun setOutcome(hiringId: String, outcome: String) {
        viewModelScope.launch {
            try {
                dbHelper.setOutcome(hiringId, outcome)
                // Ricarica la lista per mostrare lo stato aggiornato
                currentUserEmail?.let { loadHirings(it) }
            } catch (e: Exception) {
                _uiState.value = HiringUiState.Error(e.message ?: "Failed to set outcome")
            }
        }
    }

    fun setHireDone(hiringId: String) {
        viewModelScope.launch {
            try {
                dbHelper.setHireDone(hiringId)
                // Ricarica la lista per mostrare lo stato aggiornato
                currentUserEmail?.let { loadHirings(it) }
            } catch (e: Exception) {
                _uiState.value = HiringUiState.Error(e.message ?: "Failed to mark as done")
            }
        }
    }
}

// Factory per creare il ViewModel con la dipendenza DBHelper
class HiringListViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HiringListViewModel::class.java)) {
            return HiringListViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
