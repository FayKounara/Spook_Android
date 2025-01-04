package com.example.room_setup_composables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: Review)

    @Query("SELECT * FROM reviews_table")
    fun getReviews(): Flow<List<Review>>

    @Query("DELETE FROM reviews_table")
    suspend fun deleteAllReviews()

}
