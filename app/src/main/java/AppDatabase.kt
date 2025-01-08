package com.example.room_setup_composables

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/*@Database(entities = [Booking::class], version = 1, exportSchema = false)
abstract class BookingDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: BookingDatabase? = null

        fun getDatabase(context: android.content.Context): BookingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    BookingDatabase::class.java,
                    "booking_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}*/

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE booking_table ADD COLUMN phoneNumber TEXT")
        db.execSQL("ALTER TABLE booking_table ADD COLUMN persons INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE booking_table ADD COLUMN occasion TEXT NOT NULL DEFAULT ''")

        db.execSQL("ALTER TABLE store_table ADD COLUMN availability INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE users_table ADD COLUMN phoneNumber TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE users_table ADD COLUMN email TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [Booking::class, Store::class, User::class, Review::class, Offer::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun storeDao(): StoreDao
    abstract fun userDao(): UserDao
    abstract fun reviewDao(): ReviewDao
    abstract fun offerDao(): OfferDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("AppDatabase",
                                       "innnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn."
                                   )
                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Grill', 'Special Burgers and snacks', 'Sunday', '8:00 AM - 8:00 PM', 'Kolokotroni 12', 1);")
                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Pizza', 'Special Pizzas and Burgers', 'Monday', '9:00 AM - 6:00 PM', 'Leoforos Kifisias 22', 2);")
                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Pasta', 'Special Pasta and Pizza', 'Sunday', '10:00 AM - 7:00 PM', 'Ippokratous 5', 3);")

                            db.execSQL("INSERT INTO offers_table (name, description, orgPrice, discountPrice, image, storeId) \n" +
                                    "VALUES \n" +
                                    "('Pizza', 'Pizza Margarita', 15.99, 12.99, 'a', 2),\n" +
                                    "('Burger', 'Double Cheeseburger', 10.99, 8.99, 'b', 1),\n" +
                                    "('Pasta', 'Carbonara', 12.50, 9.99, 'c', 3);\n")





//                            CoroutineScope(Dispatchers.IO).launch {
//                                val storedao = getDatabase(context).storeDao()
//                                val stores = listOf(
//                                    Store(
//                                        name = "Juicy Grill",
//                                        info = "Special Burgers and snacks",
//                                        avDays = "Sunday",
//                                        avHours = "8:00 AM - 8:00 PM",
//                                        location = "Kolokotroni 12",
//                                        availability = 1
//                                    ),
//                                    Store(
//                                        name = "Juicy Pizza Holargos",
//                                        info = "Special Pizzas and Burgers",
//                                        avDays = "Monday",
//                                        avHours = "9:00 AM - 6:00 PM",
//                                        location = "Leoforos Kifisias 22",
//                                        availability = 2
//                                    ),
//                                    Store(
//                                        name = "Juicy Pasta Exarchia ",
//                                        info = "Special Pasta and Pizza",
//                                        avDays = "Sunday",
//                                        avHours = "10:00 AM - 7:00 PM",
//                                        location = "Ippokratous  5",
//                                        availability = 3
//                                    )
//
//                                )
//                                val database = INSTANCE
//                                database?.let {
//                                    val storeDao = it.storeDao()
//                                    for (store in stores) {
//                                        storeDao.insert(store)
//                                    }
//                                    Log.d(
//                                        "AppDatabase",
//                                        "prepopulate stores inserted successfully."
//                                    )
//                                }
//
//
//                            }
                        }
                    })
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }





}

