package com.example.room_setup_composables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "booking_table",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"]),
        ForeignKey(entity = Store::class, parentColumns = ["storeId"], childColumns = ["storeId"])
    ],
    indices = [Index(value = ["userId"]), Index(value = ["storeId"])]
)
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,
    val date: String,
    val hours: String,
    val storeId: Int,
    val userId: Int,
    val phoneNumber: String,
    val persons: Int,
    val occasion: String

)