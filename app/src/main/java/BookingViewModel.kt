package com.example.room_setup_composables

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/*class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).bookingDao()

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
}*/

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val bookingDao = database.bookingDao()
    private val storeDao = database.storeDao()
    private val userDao = database.userDao()

    private val _allBookings = MutableStateFlow<List<Booking>>(emptyList())
    val allBookings: StateFlow<List<Booking>> = _allBookings.asStateFlow()

    private val _allStores = MutableStateFlow<List<Store>>(emptyList())
    val allStores: StateFlow<List<Store>> = _allStores.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    init {
        fetchAllBookings()
        fetchAllStores()
        fetchAllUsers()
//        insertDummyUsers()
    }

    private fun fetchAllBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.getAllBookings().collect { bookings ->
                _allBookings.value = bookings
            }
        }
    }

    private fun fetchAllStores() {
        viewModelScope.launch(Dispatchers.IO) {
            storeDao.getAllStores().collect { stores ->
                _allStores.value = stores
            }
        }
    }

    private fun fetchAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.getAllUsers().collect { users ->
                _allUsers.value = users
            }
        }
    }

    fun insertBooking(booking: Booking) {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.insert(booking)
            fetchAllBookings()
        }
    }

    fun deleteBooking(booking: Booking) {
        viewModelScope.launch(Dispatchers.IO) {
            bookingDao.delete(booking)
            fetchAllBookings()
        }
    }

    fun insertStore(store: Store) {
        viewModelScope.launch(Dispatchers.IO) {
            storeDao.insert(store)
            fetchAllStores()
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insert(user)
            fetchAllUsers()
        }
    }

    fun insertDummyUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = listOf(
                User(userId = 1, username = "Alice", password = "123", phoneNumber = "", email = ""),
                User(userId = 2, username = "Bob", password = "123", phoneNumber = "", email = ""),
                User(
                    userId = 3,
                    username = "Charlie",
                    password = "123",
                    phoneNumber = "",
                    email = ""
                )
            )
            for (user in users) {
                userDao.insert(user)
            }
            Log.d("BookingViewModel", "Dummy users inserted")
        }
    }
}
