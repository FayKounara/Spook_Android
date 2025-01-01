package com.example.room_setup_composables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Insert
    suspend fun insert(store: Store)

    @Query("SELECT * FROM store_table")
    fun getAllStores(): Flow<List<Store>>

    @Query("DELETE FROM store_table")
    suspend fun deleteAllStores()
}
