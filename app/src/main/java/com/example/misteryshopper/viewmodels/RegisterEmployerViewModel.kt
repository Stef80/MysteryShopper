package com.example.misteryshopper.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.EmployerModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

class RegisterEmployerViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerEmployer(
        employer: EmployerModel,
        email: String,
        password: String,
        context: Context
    ) {
        if (email.isEmpty() || !email.contains("@")) {
            _registrationState.value = RegistrationState.Error("A valid email is required.")
            return
        }
        if (password.length <= 5) {
            _registrationState.value = RegistrationState.Error("Password must be longer than 5 characters.")
            return
        }

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                dbHelper.register(employer, email, password, context)
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    // Function to reset the state, e.g., after an error message has been shown
    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}

class RegisterEmployerViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterEmployerViewModel::class.java)) {
            return RegisterEmployerViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
