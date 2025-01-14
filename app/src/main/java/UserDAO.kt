package com.example.room_setup_composables


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users_table")
    fun getAllUsers(): Flow<List<User>>
    // query for profile
    @Query("SELECT username FROM users_table WHERE userId = :userId")
    fun getName(userId: Int): String ?

    @Query("SELECT email FROM users_table WHERE userId = :userId")
    fun getEmail(userId: Int): String ?
}

