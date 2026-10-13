package com.example.jurnaltreding.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.jurnaltreding.data.datastore.SessionManager
import com.example.jurnaltreding.data.local.User
import com.example.jurnaltreding.data.local.UserDao
import com.example.jurnaltreding.util.PasswordHasher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/** Data user di Room, sesi login di DataStore. */
class RoomUserRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : UserRepository {

    override val currentUserId: Flow<Long?> = sessionManager.userId

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<User?> =
        sessionManager.userId.flatMapLatest { userId ->
            if (userId == null) flowOf(null) else userDao.observeById(userId)
        }

    override suspend fun login(email: String, password: String): AuthResult {
        val user = userDao.getByEmail(normalizeEmail(email))
            ?: return AuthResult.InvalidCredentials
        if (PasswordHasher.hash(password, user.salt) != user.passwordHash) {
            return AuthResult.InvalidCredentials
        }
        sessionManager.saveUserId(user.id)
        return AuthResult.Success
    }

    override suspend fun register(name: String, email: String, password: String): AuthResult {
        val cleanEmail = normalizeEmail(email)
        if (userDao.getByEmail(cleanEmail) != null) return AuthResult.EmailAlreadyUsed

        val salt = PasswordHasher.generateSalt()
        val user = User(
            name = name.trim(),
            email = cleanEmail,
            passwordHash = PasswordHasher.hash(password, salt),
            salt = salt
        )
        val userId = try {
            userDao.insert(user)
        } catch (e: SQLiteConstraintException) {
            // Indeks unik email menolak pendaftaran ganda yang lolos pengecekan di atas.
            return AuthResult.EmailAlreadyUsed
        }
        sessionManager.saveUserId(userId)
        return AuthResult.Success
    }

    override suspend fun updateProfile(name: String, investingStyle: String?) {
        val userId = sessionManager.userId.first() ?: return
        userDao.updateProfile(
            id = userId,
            name = name.trim(),
            investingStyle = investingStyle?.trim()?.ifEmpty { null }
        )
    }

    override suspend fun logout() {
        sessionManager.clear()
    }

    private fun normalizeEmail(email: String) = email.trim().lowercase()
}
