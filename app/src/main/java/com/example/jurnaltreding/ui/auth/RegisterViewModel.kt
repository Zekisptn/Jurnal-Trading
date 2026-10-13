package com.example.jurnaltreding.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.repository.AuthResult
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.util.validateConfirmPassword
import com.example.jurnaltreding.util.validateEmail
import com.example.jurnaltreding.util.validateName
import com.example.jurnaltreding.util.validateNewPassword
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (_uiState.value.isLoading) return

        val errors = buildMap {
            validateName(name)?.let { put(AuthField.NAME, it) }
            validateEmail(email)?.let { put(AuthField.EMAIL, it) }
            validateNewPassword(password)?.let { put(AuthField.PASSWORD, it) }
            validateConfirmPassword(password, confirmPassword)?.let {
                put(AuthField.CONFIRM_PASSWORD, it)
            }
        }
        if (errors.isNotEmpty()) {
            _uiState.value = AuthUiState(fieldErrors = errors)
            return
        }

        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                when (userRepository.register(name, email, password)) {
                    // Akun baru langsung login; navigasi ke Dashboard ditangani NavGraph.
                    AuthResult.Success -> Unit
                    AuthResult.EmailAlreadyUsed -> _uiState.value = AuthUiState(
                        fieldErrors = mapOf(AuthField.EMAIL to "Email sudah terdaftar")
                    )
                    else -> _uiState.value = AuthUiState(generalError = "Gagal mendaftar. Coba lagi.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AuthUiState(generalError = "Gagal mendaftar. Coba lagi.")
            }
        }
    }

    /** Dipanggil saat user mengubah isi field supaya pesan error-nya hilang. */
    fun clearError(field: AuthField) {
        _uiState.update { it.copy(fieldErrors = it.fieldErrors - field, generalError = null) }
    }
}
