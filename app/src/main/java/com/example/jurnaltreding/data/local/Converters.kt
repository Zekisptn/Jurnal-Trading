package com.example.jurnaltreding.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

/** Tanggal disimpan sebagai jumlah hari sejak 1 Jan 1970 supaya bisa diurutkan di SQL. */
class Converters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
