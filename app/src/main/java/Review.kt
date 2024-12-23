package com.example.room_setup_composables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews_table",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"]),
        ForeignKey(entity = Store::class, parentColumns = ["storeId"], childColumns = ["storeId"])
    ],
    indices = [Index(value = ["userId"]), Index(value = ["storeId"])]
)
data class Review(
    @PrimaryKey(autoGenerate = true)
    val revId: Int = 0,
    val stars: Int,
    val revText: String,
    val userId: Int,
    val storeId: Int
)
