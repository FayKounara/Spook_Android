package com.example.room_setup_composables

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var bookingDao: BookingDao
    private lateinit var storeDao: StoreDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing purposes only
            .build()
        bookingDao = db.bookingDao()
        storeDao = db.storeDao()
        userDao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testDatabaseOperations() = runBlocking {
        // Insert sample users
        val user1 = User(
            username = "JohnDoe",
            password = "password123",
            phoneNumber = "",
            email = ""
        )//, bookings = emptyList())
        val user2 = User(username = "JaneSmith", password = "pass456", phoneNumber = "", email = "")//, bookings = emptyList())
        userDao.insert(user1)
        userDao.insert(user2)

        // Insert sample stores
        val store1 = Store(name = "Coffee Shop", info = "Great coffee!", avDays = "Mon-Sun", avHours = "8:00-18:00", location = "Main Street")
        val store2 = Store(name = "Bookstore", info = "Best books!", avDays = "Mon-Sat", avHours = "10:00-19:00", location = "Downtown")
        storeDao.insert(store1)
        storeDao.insert(store2)

        // Insert sample bookings
        val booking1 = Booking(
            date = "2024-06-30",
            hours = "12:00-14:00",
            storeId = 1,
            userId = 1,
            phoneNumber = "",
            persons = "2",
            occasion = ""
        )
        val booking2 = Booking(
            date = "2024-07-01",
            hours = "14:00-16:00",
            storeId = 2,
            userId = 2,
            phoneNumber = "",
            persons = "2",
            occasion = ""
        )
        bookingDao.insert(booking1)
        bookingDao.insert(booking2)

       /* // Fetch and print all bookings
        val bookings = bookingDao.getAllBookings().first()
        println("Bookings: $bookings")

        // Fetch and print all stores
        val stores = storeDao.getAllStores().first()
        println("Stores: $stores")

        // Fetch and print all users
        val users = userDao.getAllUsers().first()
        println("Users: $users")*/
    }
}
