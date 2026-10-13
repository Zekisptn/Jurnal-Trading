package com.example.jurnaltreding.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.ui.components.previewEntries
import com.example.jurnaltreding.ui.components.previewUser
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.JournalEntryCard
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.components.ProfitLossText
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme
import com.example.jurnaltreding.util.JournalStats
import com.example.jurnaltreding.util.calculateStats
import com.example.jurnaltreding.util.formatPercent

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddEntry: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    onSeeAllEntries: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardContent(
        uiState = uiState,
        onRetry = viewModel::load,
        onAddEntry = onAddEntry,
        onOpenEntry = onOpenEntry,
        onSeeAllEntries = onSeeAllEntries
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onRetry: () -> Unit,
    onAddEntry: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    onSeeAllEntries: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard") }) },
        floatingActionButton = {
            if (uiState is DashboardUiState.Success) {
                FloatingActionButton(onClick = onAddEntry) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah catatan")
                }
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (uiState) {
            DashboardUiState.Loading -> LoadingState(contentModifier)
            is DashboardUiState.Error -> ErrorState(
                message = uiState.message,
                modifier = contentModifier,
                onRetry = onRetry
            )
            is DashboardUiState.Success -> if (uiState.stats.totalEntries == 0) {
                Column(contentModifier) {
                    Greeting(
                        userName = uiState.userName,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )
                    EmptyState(
                        title = "Belum ada transaksi",
                        message = "Ringkasan akan muncul setelah kamu menambahkan catatan pertama.",
                        actionLabel = "Tambah catatan",
                        onAction = onAddEntry
                    )
                }
            } else {
                DashboardSummary(
                    state = uiState,
                    onOpenEntry = onOpenEntry,
                    onSeeAllEntries = onSeeAllEntries,
                    modifier = contentModifier
                )
            }
        }
    }
}

@Composable
private fun DashboardSummary(
    state: DashboardUiState.Success,
    onOpenEntry: (Long) -> Unit,
    onSeeAllEntries: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = state.stats
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            // Ruang bawah ekstra supaya kartu terakhir tidak tertutup tombol "+".
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Greeting(userName = state.userName)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(Modifier.padding(16.dp)) {
                StatLabel("Total untung/rugi")
                Spacer(Modifier.height(4.dp))
                ProfitLossText(
                    amount = stats.totalProfitLoss,
                    style = MaterialTheme.typography.headlineMedium
                )
                StatCaption("Dari ${stats.closedCount} posisi Closed")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Total transaksi",
                value = stats.totalEntries.toString(),
                caption = "${stats.closedCount} Closed",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Win rate",
                value = stats.winRate?.let { formatPercent(it) } ?: "–",
                caption = if (stats.winRate == null) {
                    "Belum ada posisi Closed"
                } else {
                    "${stats.winCount} dari ${stats.closedCount} untung"
                },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Posisi Open",
                value = stats.openCount.toString(),
                caption = "Belum dijual",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Emosi paling sering",
                value = stats.topEmotion?.label ?: "–",
                caption = "Saat membeli",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catatan terbaru",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onSeeAllEntries) { Text("Lihat semua") }
        }
        state.recentEntries.forEach { entry ->
            JournalEntryCard(entry = entry, onClick = { onOpenEntry(entry.id) })
        }
    }
}

@Composable
private fun Greeting(userName: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = if (userName.isBlank()) "Halo!" else "Halo, $userName",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Ringkasan jurnal tradingmu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StatLabel(label)
            Spacer(Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            StatCaption(caption)
        }
    }
}

@Composable
private fun StatLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StatCaption(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PreviewDashboard(uiState: DashboardUiState) {
    JurnalTredingTheme {
        DashboardContent(uiState, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, heightDp = 1000, name = "Berisi data")
@Composable
private fun DashboardPreview() {
    val entries = previewEntries()
    PreviewDashboard(
        DashboardUiState.Success(
            userName = previewUser().name,
            stats = calculateStats(entries),
            recentEntries = entries.sortedByDescending { it.buyDate }.take(3)
        )
    )
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun DashboardEmptyPreview() {
    PreviewDashboard(
        DashboardUiState.Success(
            userName = previewUser().name,
            stats = JournalStats(0, 0, 0, 0, null, 0, null),
            recentEntries = emptyList()
        )
    )
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun DashboardLoadingPreview() {
    PreviewDashboard(DashboardUiState.Loading)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun DashboardErrorPreview() {
    PreviewDashboard(DashboardUiState.Error("Gagal memuat ringkasan jurnal."))
}
