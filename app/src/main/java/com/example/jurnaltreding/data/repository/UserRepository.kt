package com.example.jurnaltreding.data.repository

import com.example.jurnaltreding.data.local.User
import kotlinx.coroutines.flow.Flow

sealed interface AuthResult {
    data object Success : AuthResult
    data object InvalidCredentials : AuthResult
    data object EmailAlreadyUsed : AuthResult
}

interface UserRepository {
    /** Id user yang sedang login, null jika belum login. */
    val currentUserId: Flow<Long?>

    /** Data user yang sedang login, ter-update otomatis saat profil berubah. */
    fun observeCurrentUser(): Flow<User?>

    suspend fun login(email: String, password: String): AuthResult

    /** Mendaftarkan akun baru dan langsung login. */
    suspend fun register(name: String, email: String, password: String): AuthResult

    suspend fun updateProfile(name: String, investingStyle: String?)

    suspend fun logout()
}
