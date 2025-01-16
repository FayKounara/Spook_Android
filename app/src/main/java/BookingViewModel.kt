package com.example.room_setup_composables

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue


class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val bookingDao = database.bookingDao()
    private val storeDao = database.storeDao()

    private val _allBookings = MutableStateFlow<List<Booking>>(emptyList())

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    var storeName by mutableStateOf("")
        private set
        private var enteredStoreId by mutableStateOf("")

    private fun fetchStoreNameById(storeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val name = storeDao.getStoreNameById(storeId)
            storeName = name ?: "Store Not Found"
        }
    }

    fun newEnteredStoreId(storeId: String) {
        enteredStoreId = storeId
        fetchStoreNameById(storeId)
    }

    private fun fetchAllBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.getAllBookings().collect { bookings ->
                _allBookings.value = bookings
            }
        }
    }

    fun getBookingsForUser(userId: Int) {
        viewModelScope.launch {
            bookingDao.getBookingsForUser(userId).collect { bookingsList ->
                _bookings.value = bookingsList
            }
        }
    }

    fun insertBooking(booking: Booking) {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.insert(booking)
            fetchAllBookings()
        }
    }

}
