package com.example.jurnaltreding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.fake.sampleEntries
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme
import com.example.jurnaltreding.ui.theme.tradingColors
import com.example.jurnaltreding.util.formatDate
import com.example.jurnaltreding.util.formatRupiah
import com.example.jurnaltreding.util.formatRupiahSigned
import com.example.jurnaltreding.util.profitLoss

/** Warna untuk nilai untung (hijau), rugi (merah), atau impas (netral). */
@Composable
fun profitLossColor(amount: Long): Color = when {
    amount > 0 -> MaterialTheme.tradingColors.profit
    amount < 0 -> MaterialTheme.tradingColors.loss
    else -> MaterialTheme.colorScheme.onSurface
}

@Composable
fun ProfitLossText(
    amount: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Text(
        text = formatRupiahSigned(amount),
        modifier = modifier,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = profitLossColor(amount)
    )
}

/** Penanda status posisi: Open (belum dijual) atau Closed (sudah dijual). */
@Composable
fun StatusBadge(isClosed: Boolean, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        color = if (isClosed) colors.surfaceVariant else colors.tertiaryContainer,
        contentColor = if (isClosed) colors.onSurfaceVariant else colors.onTertiaryContainer,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = if (isClosed) "Closed" else "Open",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun EmotionBadge(emotion: Emotion, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = emotion.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/** Ringkasan satu catatan jurnal; dipakai di Dashboard dan Daftar Jurnal. */
@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = entry.stockCode, style = MaterialTheme.typography.titleMedium)
                    StatusBadge(isClosed = entry.isClosed)
                }
                val profitLoss = entry.profitLoss()
                if (profitLoss != null) {
                    ProfitLossText(amount = profitLoss)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${entry.lots} lot × ${formatRupiah(entry.buyPrice)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Beli ${formatDate(entry.buyDate)}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                EmotionBadge(emotion = entry.emotion)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalEntryCardPreview() {
    JurnalTredingTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val samples = sampleEntries()
            JournalEntryCard(entry = samples[0], onClick = {})
            JournalEntryCard(entry = samples[1], onClick = {})
            JournalEntryCard(entry = samples[4], onClick = {})
        }
    }
}
