package com.example.misteryshopper.viewmodels

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.ShopperModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterShopperViewModel(private val dbHelper: DBHelper) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerShopper(
        shopper: ShopperModel,
        email: String,
        password: String,
        imageUri: Uri?,
        context: Context
    ) {
        if (!isValid(email, password)) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val userId = dbHelper.register(shopper, email, password, context)
                if (userId != null && imageUri != null) {
                    dbHelper.addImageToUserById(userId, imageUri, context)
                }
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    private fun isValid(email: String, pass: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            _registrationState.value = RegistrationState.Error("Email cannot be empty.")
            return false
        }
        if (TextUtils.isEmpty(pass)) {
            _registrationState.value = RegistrationState.Error("Password cannot be empty.")
            return false
        }
        if (pass.length <= 5) {
            _registrationState.value = RegistrationState.Error("Password must be longer than 5 characters.")
            return false
        }
        return true
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}


class RegisterShopperViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterShopperViewModel::class.java)) {
            return RegisterShopperViewModel(FirebaseDBHelper.instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
