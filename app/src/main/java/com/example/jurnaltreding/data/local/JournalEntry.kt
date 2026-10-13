package com.example.jurnaltreding.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Satu catatan = satu posisi trading, dari beli sampai jual. */
@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val stockCode: String,
    val buyDate: LocalDate,
    val buyPrice: Long,
    val lots: Int,
    val sellDate: LocalDate? = null,
    val sellPrice: Long? = null,
    val reason: String,
    val emotion: Emotion,
    val evaluation: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /** Status dihitung dari data jual, tidak disimpan terpisah. */
    val isClosed: Boolean
        get() = sellDate != null && sellPrice != null
}
