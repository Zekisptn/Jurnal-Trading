package com.example.jurnaltreding.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private val localeId: Locale = Locale.forLanguageTag("id-ID")
private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", localeId)

private fun thousands(value: Long): String {
    val symbols = DecimalFormatSymbols(localeId).apply { groupingSeparator = '.' }
    return DecimalFormat("#,###", symbols).format(value)
}

/** 1250000 → "Rp1.250.000", -50000 → "-Rp50.000" */
fun formatRupiah(amount: Long): String {
    val sign = if (amount < 0) "-" else ""
    return "${sign}Rp${thousands(abs(amount))}"
}

/** Seperti [formatRupiah] tetapi nilai positif diberi tanda plus: "+Rp275.000". */
fun formatRupiahSigned(amount: Long): String =
    if (amount > 0) "+${formatRupiah(amount)}" else formatRupiah(amount)

/** 13 Okt 2026 */
fun formatDate(date: LocalDate): String = date.format(dateFormatter)

fun formatDate(epochMillis: Long): String =
    formatDate(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate())

/** 0.667 → "67%" */
fun formatPercent(fraction: Double): String = "${(fraction * 100).roundToInt()}%"

/** 0.06 → "+6,0%", -0.143 → "-14,3%" */
fun formatPercentSigned(fraction: Double): String {
    val sign = if (fraction > 0) "+" else ""
    return sign + String.format(localeId, "%.1f%%", fraction * 100)
}
