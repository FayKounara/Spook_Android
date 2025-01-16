package com.example.room_setup_composables


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int,
    val username: String,
    val password: String,
    val phoneNumber: String,
    val email: String
)