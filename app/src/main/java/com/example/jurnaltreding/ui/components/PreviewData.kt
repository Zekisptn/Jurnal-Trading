package com.example.jurnaltreding.ui.components

import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.local.User
import java.time.LocalDate

/** User contoh khusus untuk @Preview. */
internal fun previewUser() = User(
    id = 1,
    name = "Investor Demo",
    email = "demo@jurnal.id",
    passwordHash = "",
    salt = "",
    investingStyle = "Swing trading"
)

/** Catatan contoh khusus untuk @Preview di Android Studio; tidak pernah masuk ke database. */
internal fun previewEntries(): List<JournalEntry> {
    val today = LocalDate.now()
    return listOf(
        JournalEntry(
            id = 1,
            userId = 1,
            stockCode = "BBCA",
            buyDate = today.minusDays(40),
            buyPrice = 9_200,
            lots = 5,
            sellDate = today.minusDays(18),
            sellPrice = 9_750,
            reason = "Laporan keuangan kuartal bagus dan harga sedang koreksi ke area support.",
            emotion = Emotion.YAKIN,
            evaluation = "Sesuai rencana. Sabar menunggu target harga tercapai."
        ),
        JournalEntry(
            id = 2,
            userId = 1,
            stockCode = "GOTO",
            buyDate = today.minusDays(30),
            buyPrice = 84,
            lots = 100,
            sellDate = today.minusDays(25),
            sellPrice = 72,
            reason = "Ramai dibahas di media sosial, takut ketinggalan kenaikan.",
            emotion = Emotion.FOMO,
            evaluation = "Beli tanpa analisis. Lain kali tunggu konfirmasi, jangan ikut-ikutan."
        ),
        JournalEntry(
            id = 3,
            userId = 1,
            stockCode = "BBRI",
            buyDate = today.minusDays(7),
            buyPrice = 4_480,
            lots = 6,
            reason = "Harga turun ke support kuat, rencana simpan sampai laporan kuartal berikutnya.",
            emotion = Emotion.YAKIN
        )
    )
}
