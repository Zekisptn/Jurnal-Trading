package com.example.jurnaltreding.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.ui.components.previewEntries
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.JournalEntryCard
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme

@Composable
fun JournalListScreen(
    viewModel: JournalListViewModel,
    onAddEntry: () -> Unit,
    onOpenEntry: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    JournalListLayout(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onRetry = viewModel::load,
        onAddEntry = onAddEntry,
        onOpenEntry = onOpenEntry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalListLayout(
    uiState: JournalListUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (JournalFilter) -> Unit,
    onRetry: () -> Unit,
    onAddEntry: () -> Unit,
    onOpenEntry: (Long) -> Unit
) {
    // Teks pencarian disimpan di sini supaya bertahan saat rotasi, lalu diteruskan ke ViewModel.
    var query by rememberSaveable { mutableStateOf(uiState.query) }
    LaunchedEffect(query) { onQueryChange(query) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Jurnal") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah catatan")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Cari kode saham, mis. BBCA") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Hapus pencarian")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Search
                )
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JournalFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.filter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }

            when (val content = uiState.content) {
                JournalListContent.Loading -> LoadingState()
                is JournalListContent.Error -> ErrorState(message = content.message, onRetry = onRetry)
                is JournalListContent.Success -> when {
                    !content.hasAnyEntry -> EmptyState(
                        title = "Belum ada catatan",
                        message = "Catat transaksi pertamamu: apa yang dibeli, kenapa, dan apa yang kamu rasakan.",
                        actionLabel = "Tambah catatan",
                        onAction = onAddEntry
                    )
                    content.entries.isEmpty() -> EmptyState(
                        title = "Tidak ditemukan",
                        message = "Tidak ada catatan yang cocok dengan pencarian atau filter.",
                        icon = Icons.Outlined.SearchOff
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        // Ruang bawah ekstra supaya kartu terakhir tidak tertutup tombol "+".
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(content.entries, key = { it.id }) { entry ->
                            JournalEntryCard(entry = entry, onClick = { onOpenEntry(entry.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewList(uiState: JournalListUiState) {
    JurnalTredingTheme {
        JournalListLayout(uiState, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalListPreview() {
    PreviewList(
        JournalListUiState(content = JournalListContent.Success(previewEntries(), hasAnyEntry = true))
    )
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun JournalListEmptyPreview() {
    PreviewList(
        JournalListUiState(content = JournalListContent.Success(emptyList(), hasAnyEntry = false))
    )
}

@Preview(showBackground = true, name = "Tidak ditemukan")
@Composable
private fun JournalListNoMatchPreview() {
    PreviewList(
        JournalListUiState(
            query = "XXXX",
            content = JournalListContent.Success(emptyList(), hasAnyEntry = true)
        )
    )
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun JournalListLoadingPreview() {
    PreviewList(JournalListUiState())
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun JournalListErrorPreview() {
    PreviewList(
        JournalListUiState(content = JournalListContent.Error("Gagal memuat catatan jurnal."))
    )
}
