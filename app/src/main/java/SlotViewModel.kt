package com.example.room_setup_composables


import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//class SlotsViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val slotDao: SlotDao = AppDatabase.getDatabase(application).slotDao()
//    private val _slotsForStore = MutableLiveData<List<Slot>>()
//    val slotsForStore: LiveData<List<Slot>> = _slotsForStore
//
//    suspend fun getSlotsForStore(storeId: Int): List<Slot> {
//        return slotDao.getSlotsForStore(storeId)
//    }
//
//    fun reduceAvailabilityByTwo(slotId: Int) {
//        viewModelScope.launch {
//            slotDao.reduceByTwo(slotId)
//        }
//    }
//
//    fun increaseAvailabilityByTwo(slotId: Int) {
//        viewModelScope.launch {
//            slotDao.increaseByTwo(slotId)
//        }
//    }
//
//    fun insertSlot(slot: Slot) {
//        viewModelScope.launch {
//            slotDao.insertSlot(slot)
//        }
//    }
//}

class SlotViewModel(private val slotDao: SlotDao, private val storeDao: StoreDao) : ViewModel() {



    suspend fun fetchSlotsForStore(storeId: Int): Flow<List<Slot>> {
        return slotDao.getSlotsForStore(storeId)
    }

    suspend fun reduceSlotAvailabilityByTwo(slotId: Int) {
       // viewModelScope.launch(Dispatchers.IO) {
            try {
                slotDao.reduceByTwo(slotId)
                Log.d("SlotViewModel", "Slot availability reduced for slotId: $slotId")
            } catch (e: Exception) {
                Log.e("SlotViewModel", "Error reducing availability", e)
            }
    }

    suspend fun increaseSlotAvailabilityByTwo(slotId: Int) {
            try {
                slotDao.increaseByTwo(slotId)
                Log.d("SlotViewModel", "Slot availability increased for slotId: $slotId")
            } catch (e: Exception) {
                Log.e("SlotViewModel", "Error increasing availability", e)
            }
    }


    class SlotViewModelFactory<ViewModel>(private val slotDao: SlotDao, private val storeDao: StoreDao) : ViewModelProvider.Factory {
        /*override*/ fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SlotViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SlotViewModel(slotDao, storeDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
