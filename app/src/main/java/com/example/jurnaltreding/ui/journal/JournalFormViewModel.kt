package com.example.jurnaltreding.ui.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.ui.navigation.Routes
import com.example.jurnaltreding.util.JournalField
import com.example.jurnaltreding.util.JournalFormInput
import com.example.jurnaltreding.util.toEntry
import com.example.jurnaltreding.util.toFormInput
import com.example.jurnaltreding.util.validateJournalForm
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface JournalFormUiState {
    /** Hanya saat mode edit: menunggu catatan lama dimuat. */
    data object Loading : JournalFormUiState
    data class Error(val message: String) : JournalFormUiState
    data object NotFound : JournalFormUiState

    /** Form siap diisi. Teks input disimpan di Composable (rememberSaveable). */
    data class Editing(
        val isEditMode: Boolean,
        val initialInput: JournalFormInput,
        val fieldErrors: Map<JournalField, String> = emptyMap(),
        val isSaving: Boolean = false,
        val saveError: String? = null,
        val isSaved: Boolean = false
    ) : JournalFormUiState
}

class JournalFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    /** Null = tambah catatan baru. */
    private val entryId: Long? =
        savedStateHandle.get<Long>(Routes.ARG_ENTRY_ID)?.takeIf { it != Routes.NO_ENTRY_ID }

    private var existingEntry: JournalEntry? = null

    private val _uiState = MutableStateFlow<JournalFormUiState>(JournalFormUiState.Loading)
    val uiState: StateFlow<JournalFormUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    /** Menyiapkan form; dipakai juga oleh tombol "Coba lagi" saat catatan gagal dimuat. */
    fun load() {
        if (entryId == null) {
            _uiState.value = JournalFormUiState.Editing(
                isEditMode = false,
                initialInput = JournalFormInput(buyDate = LocalDate.now())
            )
            return
        }

        _uiState.value = JournalFormUiState.Loading
        viewModelScope.launch {
            try {
                val userId = userRepository.currentUserId.first()
                val entry = userId?.let { journalRepository.observeEntry(it, entryId).first() }
                existingEntry = entry
                _uiState.value = if (entry == null) {
                    JournalFormUiState.NotFound
                } else {
                    JournalFormUiState.Editing(isEditMode = true, initialInput = entry.toFormInput())
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = JournalFormUiState.Error("Gagal memuat catatan.")
            }
        }
    }

    fun save(input: JournalFormInput) {
        val current = _uiState.value as? JournalFormUiState.Editing ?: return
        if (current.isSaving) return

        val errors = validateJournalForm(input)
        if (errors.isNotEmpty()) {
            _uiState.value = current.copy(fieldErrors = errors, saveError = null)
            return
        }

        _uiState.value = current.copy(fieldErrors = emptyMap(), isSaving = true, saveError = null)
        viewModelScope.launch {
            try {
                val userId = checkNotNull(userRepository.currentUserId.first())
                val existing = existingEntry
                if (existing == null) {
                    journalRepository.addEntry(input.toEntry(userId))
                } else {
                    journalRepository.updateEntry(input.toEntry(userId, existing))
                }
                _uiState.value = current.copy(fieldErrors = emptyMap(), isSaved = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    fieldErrors = emptyMap(),
                    saveError = "Gagal menyimpan catatan. Coba lagi."
                )
            }
        }
    }

    /** Dipanggil saat user mengubah isi field supaya pesan error-nya hilang. */
    fun clearError(field: JournalField) {
        _uiState.update { state ->
            if (state is JournalFormUiState.Editing) {
                state.copy(fieldErrors = state.fieldErrors - field)
            } else {
                state
            }
        }
    }
}
