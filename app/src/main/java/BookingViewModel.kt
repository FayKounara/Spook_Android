package com.example.room_setup_composables

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = BookingDatabase.getDatabase(application).bookingDao()

    private val _allBookings = MutableStateFlow<List<Booking>>(emptyList())
    val allBookings: StateFlow<List<Booking>> = _allBookings.asStateFlow()

    init {
        fetchAllBookings()
    }

    private fun fetchAllBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getAllBookings().collect { bookings ->
                _allBookings.value = bookings
            }
        }
    }

    fun insertBooking(booking: Booking) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(booking)
            fetchAllBookings()
        }
    }

    fun deleteBooking(booking: Booking) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(booking)
            fetchAllBookings()
        }
    }
}
