package com.example.jurnaltreding.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.util.JournalStats
import com.example.jurnaltreding.util.calculateStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Error(val message: String) : DashboardUiState

    /** Jika [stats] menunjukkan 0 transaksi, layar menampilkan state kosong. */
    data class Success(
        val userName: String,
        val stats: JournalStats,
        val recentEntries: List<JournalEntry>
    ) : DashboardUiState
}

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    /** Mulai mengamati data; dipakai juga oleh tombol "Coba lagi". */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            val entriesFlow = userRepository.currentUserId.flatMapLatest { userId ->
                if (userId == null) flowOf(emptyList()) else journalRepository.observeEntries(userId)
            }
            // Flow dari repository membuat ringkasan ter-update otomatis saat jurnal berubah.
            combine(userRepository.observeCurrentUser(), entriesFlow) { user, entries ->
                DashboardUiState.Success(
                    userName = user?.name.orEmpty(),
                    stats = calculateStats(entries),
                    recentEntries = entries.take(RECENT_ENTRY_COUNT)
                )
            }
                .catch { _uiState.value = DashboardUiState.Error("Gagal memuat ringkasan jurnal.") }
                .collect { _uiState.value = it }
        }
    }

    private companion object {
        const val RECENT_ENTRY_COUNT = 3
    }
}
