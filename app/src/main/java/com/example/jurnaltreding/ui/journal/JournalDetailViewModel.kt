package com.example.jurnaltreding.ui.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.ui.navigation.Routes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

sealed interface JournalDetailUiState {
    data object Loading : JournalDetailUiState
    data class Error(val message: String) : JournalDetailUiState

    /** Catatan tidak ada, mis. sudah dihapus. */
    data object NotFound : JournalDetailUiState

    data class Success(
        val entry: JournalEntry,
        val isDeleting: Boolean = false,
        val deleteError: String? = null
    ) : JournalDetailUiState

    /** Catatan berhasil dihapus; layar harus ditutup. */
    data object Deleted : JournalDetailUiState
}

class JournalDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val entryId: Long = checkNotNull(savedStateHandle[Routes.ARG_ENTRY_ID])

    private val _uiState = MutableStateFlow<JournalDetailUiState>(JournalDetailUiState.Loading)
    val uiState: StateFlow<JournalDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    /** Memuat ulang catatan; dipakai juga oleh tombol "Coba lagi". */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = JournalDetailUiState.Loading
            userRepository.currentUserId
                .flatMapLatest { userId ->
                    if (userId == null) flowOf(null) else journalRepository.observeEntry(userId, entryId)
                }
                .catch { _uiState.value = JournalDetailUiState.Error("Gagal memuat catatan.") }
                .collect { entry ->
                    val current = _uiState.value
                    val deleteInProgress = current is JournalDetailUiState.Deleted ||
                        (current is JournalDetailUiState.Success && current.isDeleting)
                    when {
                        // Hilangnya catatan saat dihapus bukan berarti "tidak ditemukan".
                        deleteInProgress -> Unit
                        entry == null -> _uiState.value = JournalDetailUiState.NotFound
                        else -> _uiState.value = JournalDetailUiState.Success(entry)
                    }
                }
        }
    }

    fun delete() {
        val current = _uiState.value as? JournalDetailUiState.Success ?: return
        if (current.isDeleting) return

        _uiState.value = current.copy(isDeleting = true, deleteError = null)
        viewModelScope.launch {
            try {
                val userId = checkNotNull(userRepository.currentUserId.first())
                journalRepository.deleteEntry(userId, entryId)
                _uiState.value = JournalDetailUiState.Deleted
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isDeleting = false,
                    deleteError = "Gagal menghapus catatan. Coba lagi."
                )
            }
        }
    }
}
