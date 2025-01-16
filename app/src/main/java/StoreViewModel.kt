package com.example.room_setup_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreViewModel(private val storeDao: StoreDao, offerDao: OfferDao) : ViewModel() {

    //al stores as flow
    val allStores: Flow<List<Store>> = storeDao.getAllStores()
    val allOffers: Flow<List<Offer>> = offerDao.getAllOffers()

    suspend fun getStoreById(storeId: Int): Store? {
        return allStores.firstOrNull()?.find { it.storeId == storeId }
    }

    private val _storeNames = MutableStateFlow<Map<Int, String?>>(emptyMap())
    val storeNames: StateFlow<Map<Int, String?>>
        get() = _storeNames

    fun fetchStoreName(storeId: Int) {
        viewModelScope.launch {
            val name = storeDao.getStoreNameById(storeId.toString())
            _storeNames.update { current ->
                current.toMutableMap().apply {
                    this[storeId] = name
                }
            }
        }
    }

    private val _storeLocations = MutableStateFlow<Map<Int, String?>>(emptyMap())
    val storeLocations: StateFlow<Map<Int, String?>>
        get() = _storeLocations

    fun fetchStoreLocation(storeId: Int) {
        viewModelScope.launch {
            val location = storeDao.getLocationById(storeId)
            _storeLocations.update { current ->
                current.toMutableMap().apply {
                    this[storeId] = location
                }
            }
        }
    }

    // Factory for creating StoreViewModel with StoreDao
    class StoreViewModelFactory(private val storeDao: StoreDao, private val offerDao: OfferDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoreViewModel(storeDao, offerDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
