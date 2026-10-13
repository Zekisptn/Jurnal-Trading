package com.example.jurnaltreding.data.repository

import com.example.jurnaltreding.data.local.JournalDao
import com.example.jurnaltreding.data.local.JournalEntry
import kotlinx.coroutines.flow.Flow

class RoomJournalRepository(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun observeEntries(userId: Long): Flow<List<JournalEntry>> =
        journalDao.observeByUser(userId)

    override fun observeEntry(userId: Long, entryId: Long): Flow<JournalEntry?> =
        journalDao.observeById(userId, entryId)

    override suspend fun addEntry(entry: JournalEntry): Long = journalDao.insert(entry)

    override suspend fun updateEntry(entry: JournalEntry) = journalDao.update(entry)

    override suspend fun deleteEntry(userId: Long, entryId: Long) =
        journalDao.delete(userId, entryId)
}
