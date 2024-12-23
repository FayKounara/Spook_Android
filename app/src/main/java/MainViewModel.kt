package com.example.room_setup_composables

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()
    private val storeDao = db.storeDao()
    private val bookingDao = db.bookingDao()

    // StateFlow for UI updates
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    init {
        fetchAllData()
    }

    // Fetch all users, stores, and bookings
    private fun fetchAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.getAllUsers().collect { _users.value = it }
            storeDao.getAllStores().collect { _stores.value = it }
            bookingDao.getAllBookings().collect { _bookings.value = it }
        }
    }

    // Insert operations
    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        userDao.insert(user)
        fetchAllData()
    }

    fun insertStore(store: Store) = viewModelScope.launch(Dispatchers.IO) {
        storeDao.insert(store)
        fetchAllData()
    }

    fun insertBooking(booking: Booking) = viewModelScope.launch(Dispatchers.IO) {
        bookingDao.insert(booking)
        fetchAllData()
    }

    fun deleteBooking(booking: Booking) = viewModelScope.launch(Dispatchers.IO) {
        bookingDao.delete(booking)
        fetchAllData()
    }
}
