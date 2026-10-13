package com.example.jurnaltreding.util

import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.local.JournalEntry

const val SHARES_PER_LOT = 100

/** Nilai pembelian = harga beli × lot × 100 lembar. */
fun JournalEntry.buyValue(): Long = buyPrice * lots * SHARES_PER_LOT

/** Untung/rugi = (harga jual − harga beli) × lot × 100. Null jika posisi masih Open. */
fun JournalEntry.profitLoss(): Long? {
    val sell = sellPrice ?: return null
    if (!isClosed) return null
    return (sell - buyPrice) * lots * SHARES_PER_LOT
}

/** Persentase untung/rugi terhadap harga beli, mis. 0.06 untuk +6%. */
fun JournalEntry.profitLossFraction(): Double? {
    val sell = sellPrice ?: return null
    if (!isClosed || buyPrice == 0L) return null
    return (sell - buyPrice).toDouble() / buyPrice
}

data class JournalStats(
    val totalEntries: Int,
    val openCount: Int,
    val closedCount: Int,
    val winCount: Int,
    /** Closed yang untung / total Closed. Null jika belum ada posisi Closed. */
    val winRate: Double?,
    val totalProfitLoss: Long,
    val topEmotion: Emotion?
)

fun calculateStats(entries: List<JournalEntry>): JournalStats {
    val closed = entries.filter { it.isClosed }
    val winCount = closed.count { (it.profitLoss() ?: 0) > 0 }
    return JournalStats(
        totalEntries = entries.size,
        openCount = entries.size - closed.size,
        closedCount = closed.size,
        winCount = winCount,
        winRate = if (closed.isEmpty()) null else winCount.toDouble() / closed.size,
        totalProfitLoss = closed.sumOf { it.profitLoss() ?: 0 },
        topEmotion = entries.groupingBy { it.emotion }.eachCount().maxByOrNull { it.value }?.key
    )
}
