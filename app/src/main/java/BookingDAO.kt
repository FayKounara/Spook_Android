package com.example.room_setup_composables

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface BookingDao {
    @Insert
    suspend fun insert(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)

    @Query("SELECT * FROM booking_table")
    fun getAllBookings(): Flow<List<Booking>>
}