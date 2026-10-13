package com.example.jurnaltreding.data.local

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val investingStyle: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
