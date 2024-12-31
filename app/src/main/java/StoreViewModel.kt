package com.example.room_setup_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.room_setup_composables.StoreDao
import com.example.room_setup_composables.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
class StoreViewModel(private val storeDao: StoreDao) : ViewModel() {

        //al stores as flow
        val allStores: Flow<List<Store>> = storeDao.getAllStores()

        // Insert a new store into the database
        fun insertStore(store: Store) {
            viewModelScope.launch {
                storeDao.insert(store)
            }
        }
    }

    // Factory for creating StoreViewModel with StoreDao
    class StoreViewModelFactory(private val storeDao: StoreDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoreViewModel(storeDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
