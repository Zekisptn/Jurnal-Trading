package com.example.jurnaltreding.data

import com.example.jurnaltreding.data.repository.JournalRepository
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.data.repository.fake.FakeJournalRepository
import com.example.jurnaltreding.data.repository.fake.FakeUserRepository

/** Dependency injection manual: satu tempat untuk membuat repository. */
interface AppContainer {
    val userRepository: UserRepository
    val journalRepository: JournalRepository
}

/** Container sementara berbasis data contoh; nanti diganti implementasi Room + DataStore. */
class FakeAppContainer : AppContainer {
    override val userRepository: UserRepository by lazy { FakeUserRepository() }
    override val journalRepository: JournalRepository by lazy { FakeJournalRepository() }
}
