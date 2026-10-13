package com.example.jurnaltreding.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Semua query difilter per userId supaya user hanya mengakses catatannya sendiri. */
@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY buyDate DESC, createdAt DESC")
    fun observeByUser(userId: Long): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :entryId AND userId = :userId")
    fun observeById(userId: Long, entryId: Long): Flow<JournalEntry?>

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    @Update
    suspend fun update(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :entryId AND userId = :userId")
    suspend fun delete(userId: Long, entryId: Long)
}
