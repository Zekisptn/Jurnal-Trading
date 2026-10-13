package com.example.jurnaltreding.data.repository.fake

import com.example.jurnaltreding.data.local.User
import com.example.jurnaltreding.data.repository.AuthResult
import com.example.jurnaltreding.data.repository.UserRepository
import com.example.jurnaltreding.util.PasswordHasher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

/** UserRepository sementara berbasis data di memori. Data hilang saat aplikasi ditutup. */
class FakeUserRepository : UserRepository {

    private val users = MutableStateFlow(listOf(demoUser()))
    private val sessionUserId = MutableStateFlow(
        if (FakeConfig.startLoggedIn) DEMO_USER_ID else null
    )

    override val currentUserId: Flow<Long?> = sessionUserId

    override fun observeCurrentUser(): Flow<User?> = flow {
        simulateDataAccess()
        emitAll(
            combine(users, sessionUserId) { list, id -> list.find { it.id == id } }
        )
    }

    override suspend fun login(email: String, password: String): AuthResult {
        delay(FakeConfig.delayMillis)
        val user = users.value.find { it.email.equals(email.trim(), ignoreCase = true) }
            ?: return AuthResult.InvalidCredentials
        if (PasswordHasher.hash(password, user.salt) != user.passwordHash) {
            return AuthResult.InvalidCredentials
        }
        sessionUserId.value = user.id
        return AuthResult.Success
    }

    override suspend fun register(name: String, email: String, password: String): AuthResult {
        delay(FakeConfig.delayMillis)
        val cleanEmail = email.trim().lowercase()
        if (users.value.any { it.email == cleanEmail }) return AuthResult.EmailAlreadyUsed

        val salt = PasswordHasher.generateSalt()
        val user = User(
            id = (users.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name.trim(),
            email = cleanEmail,
            passwordHash = PasswordHasher.hash(password, salt),
            salt = salt
        )
        users.update { it + user }
        sessionUserId.value = user.id
        return AuthResult.Success
    }

    override suspend fun updateProfile(name: String, investingStyle: String?) {
        simulateDataAccess()
        val id = sessionUserId.value ?: return
        users.update { list ->
            list.map { user ->
                if (user.id == id) {
                    user.copy(
                        name = name.trim(),
                        investingStyle = investingStyle?.trim()?.ifEmpty { null }
                    )
                } else {
                    user
                }
            }
        }
    }

    override suspend fun logout() {
        sessionUserId.value = null
    }

    private fun demoUser(): User {
        val salt = PasswordHasher.generateSalt()
        return User(
            id = DEMO_USER_ID,
            name = "Investor Demo",
            email = FakeConfig.DEMO_EMAIL,
            passwordHash = PasswordHasher.hash(FakeConfig.DEMO_PASSWORD, salt),
            salt = salt,
            investingStyle = "Swing trading"
        )
    }

    companion object {
        const val DEMO_USER_ID = 1L
    }
}
