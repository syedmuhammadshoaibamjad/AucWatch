package com.shoaib.aucwatch.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shoaib.aucwatch.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Using StateFlow for current user and other states
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> get() = _currentUser

    private val _failureMessage = MutableStateFlow<String?>(null)
    val failureMessage: StateFlow<String?> get() = _failureMessage

    private val _resetResponse = MutableStateFlow<Boolean?>(null)
    val resetResponse: StateFlow<Boolean?> get() = _resetResponse

    init {
        // Load the current user if they are already authenticated
        _currentUser.value = FirebaseAuth.getInstance().currentUser
    }

    // Login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _currentUser.value = result.getOrThrow()
            } else {
                _failureMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Reset password function
    fun resetPassword(email: String) {
        viewModelScope.launch {
            val result = authRepository.resetPassword(email)
            if (result.isSuccess) {
                _resetResponse.value = result.getOrThrow()
            } else {
                _failureMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Sign up function
    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            val result = authRepository.signup(email, password, name)
            if (result.isSuccess) {
                _currentUser.value = result.getOrThrow()
            } else {
                _failureMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Log out function
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
        }
    }
}
