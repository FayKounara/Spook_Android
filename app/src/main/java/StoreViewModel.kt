package com.example.room_setup_composables

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.room_setup_composables.StoreDao
import com.example.room_setup_composables.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StoreViewModel(private val storeDao: StoreDao, private val offerDao: OfferDao) : ViewModel() {

    //al stores as flow
    val allStores: Flow<List<Store>> = storeDao.getAllStores()
    val allOffers: Flow<List<Offer>> = offerDao.getAllOffers()

    suspend fun getStoreById(storeId: Int): Store? {
        return allStores.firstOrNull()?.find { it.storeId == storeId }
    }

    val offersWithStoreLocation: Flow<List<Pair<Offer, String>>> = allOffers.combine(allStores) { offers, stores ->
        offers.map { offer ->
            val store = stores.find { it.storeId == offer.storeId }
            Pair(offer, store?.location ?: "Unknown Location") // Επιστρέφει τοποθεσία του καταστήματος
        }
    }


    init {
//        insertDummyStores()
//        insertDummyOffers()
    }

    // Insert a new store into the database
    fun insertStore(store: Store) {
        viewModelScope.launch {
            storeDao.insert(store)
        }
    }

    /*fun resetAndInsertMockStores() {
                viewModelScope.launch(Dispatchers.IO) {
                    storeDao.deleteAllStores() // Καθαρισμός του πίνακα
                    val mockStores = listOf(
                        Store( name = "Juicy Grill", info = "Special Burgers and snacks", avDays = "Mon-Sun", avHours = "8:00 AM - 8:00 PM", location = "Kolokotroni 12"),
                        Store( name = "Juicy Pizza Holargos", info = "Special Pizzas and Burgers", avDays = "Mon-Fri", avHours = "9:00 AM - 6:00 PM", location = "Leoforos Kifisias 22"),
                        Store( name = "Juicy Pasta Exarchia ", info = "Special Pasta and Pizza", avDays = "Mon-Sat", avHours = "10:00 AM - 7:00 PM", location = "Ippokratous  5")
                    )
                    mockStores.forEach { store ->
                        storeDao.insert(store)
                    }
                }
            }*/

    fun insertDummyStores() {
        viewModelScope.launch(Dispatchers.IO) {
            //storeDao.deleteAllStores()
            val stores = listOf(
                Store(
                    name = "Juicy Grill",
                    info = "Special Burgers and snacks",
                    avDays = "Sunday",
                    avHours = "8:00 AM - 8:00 PM",
                    location = "Kolokotroni 12",
                    availability = 10
                ),
                Store(
                    name = "Juicy Pizza Holargos",
                    info = "Special Pizzas and Burgers",
                    avDays = "Monday",
                    avHours = "9:00 AM - 6:00 PM",
                    location = "Leoforos Kifisias 22",
                    availability = 10
                ),
                Store(
                    name = "Juicy Pasta Exarchia ",
                    info = "Special Pasta and Pizza",
                    avDays = "Sunday",
                    avHours = "10:00 AM - 7:00 PM",
                    location = "Ippokratous  5",
                    availability = 10
                )

            )

            for (store in stores) {
                storeDao.insert(store)
            }
            Log.d("StoreViewModel", "Dummy stores inserted")
        }
        /*data class Offer(
    @PrimaryKey(autoGenerate = true)
    val offId: Int = 0,
    val name: String,
    val description: String,
    val orgPrice: Double,
    val discountPrice: Double,
    val image: String,
    val storeId: Int
)*/

    }

    fun insertDummyOffers() {
        viewModelScope.launch(Dispatchers.IO) {
           // offerDao.deleteAllOffers()
            val offers = listOf(
                Offer( name = "Pizza", description = "Pizza Margarita", orgPrice = 15.99, discountPrice = 12.99, image = "a", storeId = 10),
                Offer( name = "Burger", description = "Double Smashed Burger", orgPrice = 10.99, discountPrice = 8.99, image = "b", storeId = 11),
                Offer( name = "Pasta", description = "Bolognese", orgPrice = 13.99, discountPrice = 10.99, image = "c", storeId = 12)
            )
            for (offer in offers) {
                offerDao.insert(offer)
            }
            Log.d("StoreViewModel", "Dummy offers inserted")
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
