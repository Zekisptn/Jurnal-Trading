package com.example.jurnaltreding.data.repository.fake

import com.example.jurnaltreding.data.local.Emotion
import com.example.jurnaltreding.data.local.JournalEntry
import com.example.jurnaltreding.data.repository.JournalRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** JournalRepository sementara berbasis data contoh di memori. */
class FakeJournalRepository : JournalRepository {

    private val entries = MutableStateFlow(
        if (FakeConfig.scenario == FakeScenario.KOSONG) emptyList() else sampleEntries()
    )

    override fun observeEntries(userId: Long): Flow<List<JournalEntry>> = flow {
        simulateDataAccess()
        emitAll(
            entries.map { list ->
                list.filter { it.userId == userId }
                    .sortedWith(
                        compareByDescending<JournalEntry> { it.buyDate }
                            .thenByDescending { it.createdAt }
                    )
            }
        )
    }

    override fun observeEntry(userId: Long, entryId: Long): Flow<JournalEntry?> = flow {
        simulateDataAccess()
        emitAll(
            entries.map { list -> list.find { it.id == entryId && it.userId == userId } }
        )
    }

    override suspend fun addEntry(entry: JournalEntry): Long {
        simulateDataAccess()
        val newId = (entries.value.maxOfOrNull { it.id } ?: 0) + 1
        entries.update { it + entry.copy(id = newId) }
        return newId
    }

    override suspend fun updateEntry(entry: JournalEntry) {
        simulateDataAccess()
        entries.update { list ->
            list.map { if (it.id == entry.id && it.userId == entry.userId) entry else it }
        }
    }

    override suspend fun deleteEntry(userId: Long, entryId: Long) {
        simulateDataAccess()
        entries.update { list -> list.filterNot { it.id == entryId && it.userId == userId } }
    }
}

/** Data contoh milik akun demo; dipakai juga oleh preview layar. */
fun sampleEntries(): List<JournalEntry> {
    val today = LocalDate.now()
    val userId = FakeUserRepository.DEMO_USER_ID
    return listOf(
        JournalEntry(
            id = 1,
            userId = userId,
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
            userId = userId,
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
            userId = userId,
            stockCode = "TLKM",
            buyDate = today.minusDays(21),
            buyPrice = 3_150,
            lots = 10,
            sellDate = today.minusDays(6),
            sellPrice = 3_320,
            reason = "Dividen rutin dan valuasi sedang murah dibanding rata-rata lima tahun.",
            emotion = Emotion.YAKIN,
            evaluation = "Target tercapai lebih cepat dari perkiraan."
        ),
        JournalEntry(
            id = 4,
            userId = userId,
            stockCode = "ANTM",
            buyDate = today.minusDays(14),
            buyPrice = 1_720,
            lots = 8,
            sellDate = today.minusDays(9),
            sellPrice = 1_640,
            reason = "Harga emas naik, berharap saham tambang ikut naik.",
            emotion = Emotion.RAGU,
            evaluation = "Panik jual saat turun sedikit. Perlu pasang batas rugi sejak awal."
        ),
        JournalEntry(
            id = 5,
            userId = userId,
            stockCode = "BBRI",
            buyDate = today.minusDays(7),
            buyPrice = 4_480,
            lots = 6,
            reason = "Harga turun ke support kuat, rencana simpan sampai laporan kuartal berikutnya.",
            emotion = Emotion.YAKIN
        ),
        JournalEntry(
            id = 6,
            userId = userId,
            stockCode = "ADRO",
            buyDate = today.minusDays(2),
            buyPrice = 2_310,
            lots = 4,
            reason = "Ikut rekomendasi grup, belum sempat cek laporan keuangannya.",
            emotion = Emotion.FOMO
        )
    )
}
