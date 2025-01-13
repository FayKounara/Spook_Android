package com.example.room_setup_composables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotDao {
    @Insert
    suspend fun insertSlot(slot: Slot)

    @Query("SELECT * FROM slots_table WHERE storeId = :storeId")
     fun getSlotsForStore(storeId: Int): Flow<List<Slot>>

    @Query("UPDATE slots_table SET availability = availability - 2 WHERE slotId = :slotId")
     fun reduceByTwo(slotId: Int)

    @Query("UPDATE slots_table SET availability = availability + 2 WHERE slotId = :slotId")
    fun increaseByTwo(slotId: Int)

    @Query("UPDATE slots_table SET availability = availability - 2 WHERE storeId = :storeId and hour = :hour")
    suspend fun newReduceByTwo(storeId: Int, hour: String)

}