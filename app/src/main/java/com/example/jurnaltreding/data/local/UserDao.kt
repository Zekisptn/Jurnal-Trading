package com.example.jurnaltreding.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    /** Gagal dengan SQLiteConstraintException jika email sudah dipakai. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: Long): Flow<User?>

    @Query("UPDATE users SET name = :name, investingStyle = :investingStyle WHERE id = :id")
    suspend fun updateProfile(id: Long, name: String, investingStyle: String?)
}
