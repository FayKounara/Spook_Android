package com.example.room_setup_composables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "slots_table",
    foreignKeys = [ForeignKey(
        entity = Store::class,
        parentColumns = ["storeId"],
        childColumns = ["storeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Slot(
    @PrimaryKey(autoGenerate = true) val slotId: Int = 0,
    val hour: Int,
    val availability: Int,
    val day: String,
    val storeId: Int
)