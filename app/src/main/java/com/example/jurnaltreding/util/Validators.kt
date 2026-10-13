package com.example.jurnaltreding.util

import java.time.LocalDate

// Setiap fungsi validasi mengembalikan pesan error, atau null jika input valid.

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")
private val STOCK_CODE_REGEX = Regex("^[A-Z]{4}$")

const val MIN_PASSWORD_LENGTH = 6

fun validateName(name: String): String? = when {
    name.isBlank() -> "Nama wajib diisi"
    name.trim().length < 2 -> "Nama minimal 2 karakter"
    else -> null
}

fun validateEmail(email: String): String? = when {
    email.isBlank() -> "Email wajib diisi"
    !EMAIL_REGEX.matches(email.trim()) -> "Format email tidak valid"
    else -> null
}

/** Untuk login: cukup pastikan terisi. */
fun validatePasswordFilled(password: String): String? =
    if (password.isEmpty()) "Password wajib diisi" else null

fun validateNewPassword(password: String): String? = when {
    password.isEmpty() -> "Password wajib diisi"
    password.length < MIN_PASSWORD_LENGTH -> "Password minimal $MIN_PASSWORD_LENGTH karakter"
    else -> null
}

fun validateConfirmPassword(password: String, confirmation: String): String? = when {
    confirmation.isEmpty() -> "Konfirmasi password wajib diisi"
    confirmation != password -> "Konfirmasi password tidak sama"
    else -> null
}

fun validateStockCode(code: String): String? = when {
    code.isBlank() -> "Kode saham wajib diisi"
    !STOCK_CODE_REGEX.matches(code.trim().uppercase()) -> "Kode saham harus 4 huruf, mis. BBCA"
    else -> null
}

fun validatePrice(text: String, label: String): String? {
    if (text.isBlank()) return "$label wajib diisi"
    val value = text.trim().toLongOrNull() ?: return "$label harus berupa angka"
    return if (value <= 0) "$label harus lebih dari 0" else null
}

fun validateLots(text: String): String? {
    if (text.isBlank()) return "Jumlah lot wajib diisi"
    val value = text.trim().toIntOrNull() ?: return "Jumlah lot harus berupa angka"
    return if (value < 1) "Jumlah lot minimal 1" else null
}

fun validateBuyDate(date: LocalDate?, today: LocalDate = LocalDate.now()): String? = when {
    date == null -> "Tanggal beli wajib diisi"
    date.isAfter(today) -> "Tanggal beli tidak boleh di masa depan"
    else -> null
}

fun validateSellDate(
    sellDate: LocalDate?,
    buyDate: LocalDate?,
    today: LocalDate = LocalDate.now()
): String? = when {
    sellDate == null -> "Tanggal jual wajib diisi"
    buyDate != null && sellDate.isBefore(buyDate) -> "Tanggal jual tidak boleh sebelum tanggal beli"
    sellDate.isAfter(today) -> "Tanggal jual tidak boleh di masa depan"
    else -> null
}

fun validateReason(reason: String): String? =
    if (reason.isBlank()) "Alasan membeli wajib diisi" else null
