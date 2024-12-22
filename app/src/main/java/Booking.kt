package com.example.room_setup_composables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_table")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerName: String,
    val reservationDate: String
)
