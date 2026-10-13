package com.example.jurnaltreding.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.local.User
import com.example.jurnaltreding.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState

    /** Tidak ada data user untuk sesi ini. */
    data object NotFound : ProfileUiState

    data class Success(
        val user: User,
        val isLoggingOut: Boolean = false
    ) : ProfileUiState
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    /** Mulai mengamati data user; dipakai juga oleh tombol "Coba lagi". */
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            userRepository.observeCurrentUser()
                .catch { _uiState.value = ProfileUiState.Error("Gagal memuat profil.") }
                .collect { user ->
                    val current = _uiState.value
                    when {
                        user != null -> _uiState.value = ProfileUiState.Success(user)
                        // User menjadi null karena logout, bukan karena datanya hilang.
                        current is ProfileUiState.Success && current.isLoggingOut -> Unit
                        else -> _uiState.value = ProfileUiState.NotFound
                    }
                }
        }
    }

    /** Menghapus sesi; perpindahan ke layar Login ditangani NavGraph. */
    fun logout() {
        val current = _uiState.value
        if (current is ProfileUiState.Success) {
            if (current.isLoggingOut) return
            _uiState.value = current.copy(isLoggingOut = true)
        }
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
