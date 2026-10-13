package com.example.jurnaltreding.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/** Menyimpan id user yang sedang login supaya sesi bertahan setelah aplikasi ditutup. */
class SessionManager(context: Context) {

    private val dataStore = context.applicationContext.sessionDataStore

    /** Id user yang sedang login, null jika belum login. */
    val userId: Flow<Long?> = dataStore.data
        // File sesi yang gagal dibaca diperlakukan sebagai belum login.
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { preferences -> preferences[USER_ID] }
        .distinctUntilChanged()

    suspend fun saveUserId(userId: Long) {
        dataStore.edit { it[USER_ID] = userId }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(USER_ID) }
    }

    private companion object {
        val USER_ID = longPreferencesKey("user_id")
    }
}
