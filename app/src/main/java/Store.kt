package com.example.room_setup_composables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "store_table")
data class Store(
    @PrimaryKey(autoGenerate = true)
    val storeId: Int = 0,
    val name: String,
    val info: String,
    val avDays: String,
    val avHours: String,
    val location: String,
)