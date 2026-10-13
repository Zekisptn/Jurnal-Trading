package com.example.jurnaltreding.util

import java.security.MessageDigest
import java.security.SecureRandom

/** Hash password dengan SHA-256 + salt. Password tidak pernah disimpan sebagai plain text. */
object PasswordHasher {

    private const val SALT_BYTES = 16

    fun generateSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest((salt + password).toByteArray(Charsets.UTF_8)).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
