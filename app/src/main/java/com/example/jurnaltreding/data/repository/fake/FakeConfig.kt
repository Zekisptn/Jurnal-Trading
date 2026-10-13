package com.example.jurnaltreding.data.repository.fake

import java.io.IOException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay

/** Skenario data contoh untuk mendemokan state layar. */
enum class FakeScenario {
    /** Data contoh tampil setelah jeda singkat. */
    NORMAL,

    /** Akun demo tanpa catatan jurnal. */
    KOSONG,

    /** Data tidak pernah selesai dimuat, layar tetap di state loading. */
    LOADING,

    /** Memuat dan menyimpan data selalu gagal. */
    ERROR
}

/**
 * Pengaturan repository sementara. Ubah nilainya lalu jalankan ulang aplikasi.
 * Login dan register tidak terpengaruh skenario supaya layar lain tetap bisa dibuka.
 */
object FakeConfig {
    var scenario: FakeScenario = FakeScenario.NORMAL

    /** Jeda buatan supaya state loading sempat terlihat. */
    var delayMillis: Long = 800L

    /** true = langsung masuk sebagai akun demo tanpa lewat layar Login. */
    var startLoggedIn: Boolean = false

    const val DEMO_EMAIL = "demo@jurnal.id"
    const val DEMO_PASSWORD = "demo1234"
}

/** Meniru jeda, kegagalan, atau loading tanpa akhir sesuai [FakeConfig.scenario]. */
internal suspend fun simulateDataAccess() {
    delay(FakeConfig.delayMillis)
    when (FakeConfig.scenario) {
        FakeScenario.LOADING -> awaitCancellation()
        FakeScenario.ERROR -> throw IOException("Simulasi kegagalan data")
        FakeScenario.NORMAL, FakeScenario.KOSONG -> Unit
    }
}
