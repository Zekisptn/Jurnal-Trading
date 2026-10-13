package com.example.jurnaltreding.data

import android.content.Context
import com.example.jurnaltreding.data.datastore.SessionManager
import com.example.jurnaltreding.data.local.AppDatabase
import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.RoomJournalRepository
import com.example.jurnaltreding.data.repository.RoomUserRepository
import com.example.jurnaltreding.data.repository.UserRepository

/** Dependency injection manual: satu tempat untuk membuat repository. */
interface AppContainer {
    val userRepository: UserRepository
    val journalRepository: JournalRepository
}

/** Container utama: data di Room, sesi login di DataStore. */
class AppDataContainer(context: Context) : AppContainer {

    private val database: AppDatabase by lazy { AppDatabase.create(context) }
    private val sessionManager: SessionManager by lazy { SessionManager(context) }

    override val userRepository: UserRepository by lazy {
        RoomUserRepository(database.userDao(), sessionManager)
    }

    override val journalRepository: JournalRepository by lazy {
        RoomJournalRepository(database.journalDao())
    }
}
