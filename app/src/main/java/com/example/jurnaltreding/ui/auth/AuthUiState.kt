package com.example.jurnaltreding.ui.auth

enum class AuthField { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

/** State layar Login dan Register. Teks input disimpan di Composable (rememberSaveable). */
data class AuthUiState(
    val fieldErrors: Map<AuthField, String> = emptyMap(),
    val isLoading: Boolean = false,
    /** Error yang bukan milik satu field, mis. email atau password salah. */
    val generalError: String? = null
)
