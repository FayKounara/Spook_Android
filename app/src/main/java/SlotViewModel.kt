package com.example.room_setup_composables


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SlotsViewModel(application: Application) : AndroidViewModel(application) {

    private val slotDao: SlotDao = AppDatabase.getDatabase(application).slotDao()

    suspend fun getSlotsForStore(storeId: Int): List<Slot> {
        return slotDao.getSlotsForStore(storeId)
    }

    fun reduceAvailabilityByTwo(slotId: Int) {
        viewModelScope.launch {
            slotDao.reduceByTwo(slotId)
        }
    }

    fun increaseAvailabilityByTwo(slotId: Int) {
        viewModelScope.launch {
            slotDao.increaseByTwo(slotId)
        }
    }

    fun insertSlot(slot: Slot) {
        viewModelScope.launch {
            slotDao.insertSlot(slot)
        }
    }
}
