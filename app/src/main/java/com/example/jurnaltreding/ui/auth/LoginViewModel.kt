package com.example.jurnaltreding.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.repository.AuthResult
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.util.validateEmail
import com.example.jurnaltreding.util.validatePasswordFilled
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value.isLoading) return

        val errors = buildMap {
            validateEmail(email)?.let { put(AuthField.EMAIL, it) }
            validatePasswordFilled(password)?.let { put(AuthField.PASSWORD, it) }
        }
        if (errors.isNotEmpty()) {
            _uiState.value = AuthUiState(fieldErrors = errors)
            return
        }

        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            try {
                when (userRepository.login(email, password)) {
                    // Sesi berubah menjadi login; navigasi ke Dashboard ditangani NavGraph.
                    AuthResult.Success -> Unit
                    else -> _uiState.value = AuthUiState(generalError = "Email atau password salah")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AuthUiState(generalError = "Gagal masuk. Coba lagi.")
            }
        }
    }

    /** Dipanggil saat user mengubah isi field supaya pesan error-nya hilang. */
    fun clearError(field: AuthField) {
        _uiState.update { it.copy(fieldErrors = it.fieldErrors - field, generalError = null) }
    }
}
