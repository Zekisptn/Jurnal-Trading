package com.example.jurnaltreding.util

import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.local.JournalEntry
import java.time.LocalDate

enum class JournalField { STOCK_CODE, BUY_DATE, BUY_PRICE, LOTS, REASON, EMOTION, SELL_DATE, SELL_PRICE }

/** Isi form jurnal apa adanya dari UI; angka masih berupa teks sampai lolos validasi. */
data class JournalFormInput(
    val stockCode: String = "",
    val buyDate: LocalDate? = null,
    val buyPrice: String = "",
    val lots: String = "",
    val reason: String = "",
    val emotion: Emotion? = null,
    /** true jika saham sudah dijual, sehingga data jual wajib diisi. */
    val isSold: Boolean = false,
    val sellDate: LocalDate? = null,
    val sellPrice: String = "",
    val evaluation: String = ""
)

/** Mengembalikan pesan error per field; map kosong berarti form valid. */
fun validateJournalForm(
    input: JournalFormInput,
    today: LocalDate = LocalDate.now()
): Map<JournalField, String> = buildMap {
    validateStockCode(input.stockCode)?.let { put(JournalField.STOCK_CODE, it) }
    validateBuyDate(input.buyDate, today)?.let { put(JournalField.BUY_DATE, it) }
    validatePrice(input.buyPrice, "Harga beli")?.let { put(JournalField.BUY_PRICE, it) }
    validateLots(input.lots)?.let { put(JournalField.LOTS, it) }
    validateReason(input.reason)?.let { put(JournalField.REASON, it) }
    if (input.emotion == null) put(JournalField.EMOTION, "Pilih emosi saat membeli")
    if (input.isSold) {
        validateSellDate(input.sellDate, input.buyDate, today)?.let { put(JournalField.SELL_DATE, it) }
        validatePrice(input.sellPrice, "Harga jual")?.let { put(JournalField.SELL_PRICE, it) }
    }
}

fun JournalEntry.toFormInput() = JournalFormInput(
    stockCode = stockCode,
    buyDate = buyDate,
    buyPrice = buyPrice.toString(),
    lots = lots.toString(),
    reason = reason,
    emotion = emotion,
    isSold = isClosed,
    sellDate = sellDate,
    sellPrice = sellPrice?.toString().orEmpty(),
    evaluation = evaluation.orEmpty()
)

/**
 * Mengubah input yang sudah lolos [validateJournalForm] menjadi JournalEntry.
 * Isi [existing] saat mengedit supaya id dan waktu dibuat tetap sama.
 */
fun JournalFormInput.toEntry(userId: Long, existing: JournalEntry? = null): JournalEntry {
    val now = System.currentTimeMillis()
    return JournalEntry(
        id = existing?.id ?: 0,
        userId = userId,
        stockCode = stockCode.trim().uppercase(),
        buyDate = requireNotNull(buyDate),
        buyPrice = buyPrice.trim().toLong(),
        lots = lots.trim().toInt(),
        sellDate = if (isSold) sellDate else null,
        sellPrice = if (isSold) sellPrice.trim().toLong() else null,
        reason = reason.trim(),
        emotion = requireNotNull(emotion),
        evaluation = evaluation.trim().ifEmpty { null },
        createdAt = existing?.createdAt ?: now,
        updatedAt = now
    )
}
