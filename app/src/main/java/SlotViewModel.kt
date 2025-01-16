package com.example.room_setup_composables


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first


class SlotViewModel(private val slotDao: SlotDao, private val storeDao: StoreDao) : ViewModel() {

    private val _slots = MutableStateFlow<List<Slot>>(emptyList())
    val slots: StateFlow<List<Slot>> = _slots

    suspend  fun fetchSlotsForStore(storeId: Int): List<Slot> {
        return slotDao.getSlotsForStore(storeId).first()

    }
      fun fetchSlotsForStore1(storeId: Int) {
        viewModelScope.launch {
          slotDao.getSlotsForStore(storeId).collect { slotList ->
              _slots.value = slotList
           }
         }

    }

    fun reduceSlotAvailability(storeId: Int, hour: String) {
        viewModelScope.launch {
            slotDao.newReduceByTwo(storeId, hour)
            Log.d("SlotViewModel", "Slot availability reduced for storeId: $storeId, hour: $hour")

        }
    }

    class SlotViewModelFactory(
        private val slotDao: SlotDao,
        private val storeDao: StoreDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SlotViewModel::class.java)) {

                return SlotViewModel(slotDao, storeDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
