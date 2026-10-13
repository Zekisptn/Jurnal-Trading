package com.example.jurnaltreding.data.local

import java.time.LocalDate

/** Satu catatan = satu posisi trading, dari beli sampai jual. */
data class JournalEntry(
    val id: Long = 0,
    val userId: Long,
    val stockCode: String,
    val buyDate: LocalDate,
    val buyPrice: Long,
    val lots: Int,
    val sellDate: LocalDate? = null,
    val sellPrice: Long? = null,
    val reason: String,
    val emotion: Emotion,
    val evaluation: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /** Status dihitung dari data jual, tidak disimpan terpisah. */
    val isClosed: Boolean
        get() = sellDate != null && sellPrice != null
}
