package com.example.jurnaltreding.data.repository

import com.example.jurnaltreding.data.local.JournalEntry
import kotlinx.coroutines.flow.Flow

/** Semua operasi difilter per userId supaya user hanya melihat catatannya sendiri. */
interface JournalRepository {
    /** Semua catatan milik user, terbaru lebih dulu. */
    fun observeEntries(userId: Long): Flow<List<JournalEntry>>

    fun observeEntry(userId: Long, entryId: Long): Flow<JournalEntry?>

    /** Mengembalikan id catatan yang baru dibuat. */
    suspend fun addEntry(entry: JournalEntry): Long

    suspend fun updateEntry(entry: JournalEntry)

    suspend fun deleteEntry(userId: Long, entryId: Long)
}
