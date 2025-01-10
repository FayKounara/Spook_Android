package com.example.room_setup_composables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SlotDao {
    @Insert
    suspend fun insertSlot(slot: Slot)

    @Query("SELECT * FROM slots_table WHERE storeId = :storeId")
    suspend fun getSlotsForStore(storeId: Int): List<Slot>

    @Query("UPDATE slots_table SET availability = availability - 2 WHERE slotId = :slotId")
    suspend fun reduceByTwo(slotId: Int)

    @Query("UPDATE slots_table SET availability = availability + 2 WHERE slotId = :slotId")
    suspend fun increaseByTwo(slotId: Int)
}