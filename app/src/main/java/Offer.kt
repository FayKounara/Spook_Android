package com.example.room_setup_composables


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "offers_table",
    foreignKeys = [ForeignKey(entity = Store::class, parentColumns = ["storeId"], childColumns = ["storeId"])],
    indices = [Index(value = ["storeId"])]
)
data class Offer(
    @PrimaryKey(autoGenerate = true)
    val offId: Int = 0,
    val name: String,
    val description: String,
    val orgPrice: Double,
    val discountPrice: Double,
    //val image: String,
    val storeId: Int
)
