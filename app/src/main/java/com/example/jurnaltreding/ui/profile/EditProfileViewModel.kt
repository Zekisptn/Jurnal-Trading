package com.example.jurnaltreding.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.util.validateName
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
    data object NotFound : EditProfileUiState

    /** Form siap diisi. Teks input disimpan di Composable (rememberSaveable). */
    data class Editing(
        val email: String,
        val initialName: String,
        val initialInvestingStyle: String,
        val nameError: String? = null,
        val isSaving: Boolean = false,
        val saveError: String? = null,
        val isSaved: Boolean = false
    ) : EditProfileUiState
}

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    /** Memuat data user ke form; dipakai juga oleh tombol "Coba lagi". */
    fun load() {
        _uiState.value = EditProfileUiState.Loading
        viewModelScope.launch {
            try {
                val user = userRepository.observeCurrentUser().first()
                _uiState.value = if (user == null) {
                    EditProfileUiState.NotFound
                } else {
                    EditProfileUiState.Editing(
                        email = user.email,
                        initialName = user.name,
                        initialInvestingStyle = user.investingStyle.orEmpty()
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error("Gagal memuat profil.")
            }
        }
    }

    fun save(name: String, investingStyle: String) {
        val current = _uiState.value as? EditProfileUiState.Editing ?: return
        if (current.isSaving) return

        val nameError = validateName(name)
        if (nameError != null) {
            _uiState.value = current.copy(nameError = nameError, saveError = null)
            return
        }

        _uiState.value = current.copy(nameError = null, isSaving = true, saveError = null)
        viewModelScope.launch {
            try {
                userRepository.updateProfile(name, investingStyle.ifBlank { null })
                _uiState.value = current.copy(nameError = null, isSaved = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    nameError = null,
                    saveError = "Gagal menyimpan profil. Coba lagi."
                )
            }
        }
    }

    /** Dipanggil saat user mengubah nama supaya pesan error-nya hilang. */
    fun clearNameError() {
        _uiState.update { state ->
            if (state is EditProfileUiState.Editing) state.copy(nameError = null) else state
        }
    }
}
