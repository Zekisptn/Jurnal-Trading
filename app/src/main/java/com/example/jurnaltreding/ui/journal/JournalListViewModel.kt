package com.example.jurnaltreding.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class JournalFilter(val label: String) {
    SEMUA("Semua"),
    OPEN("Open"),
    CLOSED("Closed")
}

sealed interface JournalListContent {
    data object Loading : JournalListContent
    data class Error(val message: String) : JournalListContent

    /**
     * [entries] sudah melewati pencarian dan filter.
     * [hasAnyEntry] membedakan "belum ada catatan" dari "tidak ada yang cocok".
     */
    data class Success(
        val entries: List<JournalEntry>,
        val hasAnyEntry: Boolean
    ) : JournalListContent
}

data class JournalListUiState(
    val query: String = "",
    val filter: JournalFilter = JournalFilter.SEMUA,
    val content: JournalListContent = JournalListContent.Loading
)

class JournalListViewModel(
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private sealed interface Source {
        data object Loading : Source
        data object Error : Source
        data class Loaded(val entries: List<JournalEntry>) : Source
    }

    private val source = MutableStateFlow<Source>(Source.Loading)
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(JournalFilter.SEMUA)
    private var loadJob: Job? = null

    val uiState: StateFlow<JournalListUiState> =
        combine(source, query, filter) { source, query, filter ->
            JournalListUiState(
                query = query,
                filter = filter,
                content = when (source) {
                    Source.Loading -> JournalListContent.Loading
                    Source.Error -> JournalListContent.Error("Gagal memuat catatan jurnal.")
                    is Source.Loaded -> JournalListContent.Success(
                        entries = source.entries.filter { it.matches(query, filter) },
                        hasAnyEntry = source.entries.isNotEmpty()
                    )
                }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JournalListUiState())

    init {
        load()
    }

    /** Memuat ulang daftar; dipakai juga oleh tombol "Coba lagi". */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            source.value = Source.Loading
            userRepository.currentUserId
                .flatMapLatest { userId ->
                    if (userId == null) flowOf(emptyList()) else journalRepository.observeEntries(userId)
                }
                .catch { source.value = Source.Error }
                .collect { entries -> source.value = Source.Loaded(entries) }
        }
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onFilterChange(newFilter: JournalFilter) {
        filter.value = newFilter
    }

    private fun JournalEntry.matches(query: String, filter: JournalFilter): Boolean {
        val matchesFilter = when (filter) {
            JournalFilter.SEMUA -> true
            JournalFilter.OPEN -> !isClosed
            JournalFilter.CLOSED -> isClosed
        }
        return matchesFilter && stockCode.contains(query.trim(), ignoreCase = true)
    }
}
