package com.example.room_setup_composables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface OfferDao {
    @Insert
    suspend fun insert(offer: Offer)

    @Query("SELECT * FROM offers_table WHERE storeId = :storeId")
    fun getOffersForStore(storeId: Int): Flow<List<Offer>>
}
