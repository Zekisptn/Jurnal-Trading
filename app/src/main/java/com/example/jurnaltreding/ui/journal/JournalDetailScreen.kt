package com.example.jurnaltreding.ui.journal

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.fake.sampleEntries
import com.example.jurnaltreding.ui.components.ConfirmDialog
import com.example.jurnaltreding.ui.components.EmotionBadge
import com.example.jurnaltreding.ui.components.EmptyState
import com.example.jurnaltreding.ui.components.ErrorBanner
import com.example.jurnaltreding.ui.components.ErrorState
import com.example.jurnaltreding.ui.components.LoadingState
import com.example.jurnaltreding.ui.components.ProfitLossText
import com.example.jurnaltreding.ui.components.StatusBadge
import com.example.jurnaltreding.ui.components.profitLossColor
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme
import com.example.jurnaltreding.util.SHARES_PER_LOT
import com.example.jurnaltreding.util.buyValue
import com.example.jurnaltreding.util.formatDate
import com.example.jurnaltreding.util.formatPercentSigned
import com.example.jurnaltreding.util.formatRupiah
import com.example.jurnaltreding.util.profitLoss
import com.example.jurnaltreding.util.profitLossFraction

@Composable
fun JournalDetailScreen(
    viewModel: JournalDetailViewModel,
    onNavigateBack: () -> Unit,
    onEditEntry: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is JournalDetailUiState.Deleted) onNavigateBack()
    }

    JournalDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditEntry = onEditEntry,
        onDelete = viewModel::delete,
        onRetry = viewModel::load
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalDetailContent(
    uiState: JournalDetailUiState,
    onNavigateBack: () -> Unit,
    onEditEntry: (Long) -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val success = uiState as? JournalDetailUiState.Success

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Catatan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (success != null) {
                        IconButton(
                            onClick = { onEditEntry(success.entry.id) },
                            enabled = !success.isDeleting
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit catatan")
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !success.isDeleting
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Hapus catatan")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (uiState) {
            JournalDetailUiState.Loading, JournalDetailUiState.Deleted -> LoadingState(contentModifier)
            is JournalDetailUiState.Error -> ErrorState(
                message = uiState.message,
                modifier = contentModifier,
                onRetry = onRetry
            )
            JournalDetailUiState.NotFound -> EmptyState(
                title = "Catatan tidak ditemukan",
                message = "Catatan ini mungkin sudah dihapus.",
                modifier = contentModifier,
                actionLabel = "Kembali",
                onAction = onNavigateBack
            )
            is JournalDetailUiState.Success -> Column(contentModifier) {
                if (uiState.isDeleting) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                EntryDetail(entry = uiState.entry, deleteError = uiState.deleteError)
            }
        }
    }

    if (showDeleteDialog && success != null) {
        ConfirmDialog(
            title = "Hapus catatan?",
            message = "Catatan ${success.entry.stockCode} akan dihapus permanen dan tidak bisa dikembalikan.",
            confirmLabel = "Hapus",
            isDestructive = true,
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun EntryDetail(entry: JournalEntry, deleteError: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (deleteError != null) {
            ErrorBanner(message = deleteError)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = entry.stockCode, style = MaterialTheme.typography.headlineMedium)
            StatusBadge(isClosed = entry.isClosed)
        }

        val profitLoss = entry.profitLoss()
        val fraction = entry.profitLossFraction()
        if (profitLoss != null && fraction != null) {
            SectionCard(title = if (profitLoss < 0) "Rugi" else "Untung") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ProfitLossText(amount = profitLoss, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = formatPercentSigned(fraction),
                        style = MaterialTheme.typography.titleSmall,
                        color = profitLossColor(profitLoss)
                    )
                }
            }
        }

        SectionCard(title = "Pembelian") {
            DetailRow("Tanggal beli", formatDate(entry.buyDate))
            DetailRow("Harga beli", formatRupiah(entry.buyPrice))
            DetailRow("Jumlah", "${entry.lots} lot (${entry.lots * SHARES_PER_LOT} lembar)")
            DetailRow("Nilai beli", formatRupiah(entry.buyValue()))
        }

        SectionCard(title = "Penjualan") {
            val sellDate = entry.sellDate
            val sellPrice = entry.sellPrice
            if (sellDate != null && sellPrice != null) {
                DetailRow("Tanggal jual", formatDate(sellDate))
                DetailRow("Harga jual", formatRupiah(sellPrice))
                DetailRow("Nilai jual", formatRupiah(sellPrice * entry.lots * SHARES_PER_LOT))
            } else {
                Text(
                    text = "Posisi masih Open. Edit catatan ini setelah saham dijual.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionCard(title = "Alasan membeli") {
            Text(text = entry.reason, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emosi saat membeli",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                EmotionBadge(emotion = entry.emotion)
            }
        }

        SectionCard(title = "Evaluasi") {
            Text(
                text = entry.evaluation ?: "Belum ada evaluasi.",
                style = MaterialTheme.typography.bodyLarge,
                color = if (entry.evaluation == null) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PreviewDetail(uiState: JournalDetailUiState) {
    JurnalTredingTheme {
        JournalDetailContent(uiState, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun JournalDetailClosedPreview() {
    PreviewDetail(JournalDetailUiState.Success(sampleEntries()[0]))
}

@Preview(showBackground = true, name = "Open")
@Composable
private fun JournalDetailOpenPreview() {
    PreviewDetail(JournalDetailUiState.Success(sampleEntries()[4]))
}

@Preview(showBackground = true, name = "Kosong")
@Composable
private fun JournalDetailNotFoundPreview() {
    PreviewDetail(JournalDetailUiState.NotFound)
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun JournalDetailLoadingPreview() {
    PreviewDetail(JournalDetailUiState.Loading)
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun JournalDetailErrorPreview() {
    PreviewDetail(JournalDetailUiState.Error("Gagal memuat catatan."))
}
