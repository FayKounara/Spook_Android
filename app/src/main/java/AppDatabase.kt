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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS slots_table (
                slotId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                hour INTEGER NOT NULL,
                availability INTEGER NOT NULL,
                day TEXT NOT NULL,
                storeId INTEGER NOT NULL,
                FOREIGN KEY(storeId) REFERENCES store_table(storeId) ON DELETE CASCADE
            )
            """
        )

        db.execSQL(
            """
            CREATE TABLE store_table_new (
                storeId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                info TEXT NOT NULL,
                location TEXT NOT NULL
            )
            """
        )

        db.execSQL(
            """
            INSERT INTO store_table_new (storeId, name, info, location)
            SELECT storeId, name, info, location FROM store_table
            """
        )

        // Remove the old store_table
        db.execSQL("DROP TABLE store_table")

        // Rename the new table to store_table
        db.execSQL("ALTER TABLE store_table_new RENAME TO store_table")
    }
}

@Database(
    entities = [Booking::class, Store::class, User::class, Review::class, Offer::class, Slot::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun storeDao(): StoreDao
    abstract fun userDao(): UserDao
    abstract fun reviewDao(): ReviewDao
    abstract fun offerDao(): OfferDao
    abstract fun slotDao(): SlotDao

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
//                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Grill', 'Special Burgers and snacks', 'Monday', '21', 'Kolokotroni 12', 1);")
//                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Pizza', 'Special Pizzas and Burgers', 'Monday', '21', 'Leoforos Kifisias 22', 2);")
//                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Pizza', 'Special Pizzas and Burgers', 'Monday', '21', 'Leoforos Kifisias 22', 3);")
//                            db.execSQL("INSERT INTO store_table (name, info, avDays, avHours, location, availability) VALUES ('Juicy Pasta', 'Special Pasta and Pizza', 'S', '21', 'Ippokratous 5', 3);")

                            db.execSQL("INSERT INTO store_table (name, info, location) VALUES ('Juicy Grill', 'Special Burgers and snacks', 'Kolokotroni 12');")
                            db.execSQL("INSERT INTO store_table (name, info, location) VALUES ('Juicy Pizza', 'Special Pizzas and Burgers', 'Leoforos Kifisias 22');")
                            db.execSQL("INSERT INTO store_table (name, info, location) VALUES ('Juicy Burger', 'Special Pizzas and Burgers', 'Leoforos Alexandras 119');")
                            db.execSQL("INSERT INTO store_table (name, info, location) VALUES ('Juicy Pasta', 'Special Pasta and Pizza', 'Ippokratous 5');")


                            db.execSQL("INSERT INTO offers_table (name, description, orgPrice, discountPrice, image, storeId) \n" +
                                    "VALUES \n" +
                                    "('Pizza', 'Pizza Margarita', 15.99, 12.99, 'a', 2),\n" +
                                    "('Burger', 'Double Cheeseburger', 10.99, 8.99, 'b', 1),\n" +
                                    "('Pasta', 'Carbonara', 12.50, 9.99, 'c', 3);\n")

                            db.execSQL(
                                """
                                    INSERT INTO users_table (username, password, phoneNumber, email) VALUES
                                    ('john_doe', 'password123', '1234567890', 'john.doe@example.com'),
                                    ('jane_smith', 'mypassword', '0987654321', 'jane.smith@example.com'),
                                    ('mike_brown', 'qwerty123', '1122334455', 'mike.brown@example.com'),
                                    ('emily_davis', 'abc12345', '5566778899', 'emily.davis@example.com'),
                                    ('david_jones', 'letmein', '6677889900', 'david.jones@example.com'),
                                    ('sarah_lee', 'password1', '7788990011', 'sarah.lee@example.com'),
                                    ('chris_white', 'simplepass', '8899001122', 'chris.white@example.com'),
                                    ('linda_clark', 'pass1234', '9900112233', 'linda.clark@example.com'),
                                    ('robert_hall', 'easy12345', '1122003344', 'robert.hall@example.com'),
                                    ('laura_martin', '1234abcd', '3344556677', 'laura.martin@example.com');
                                    """
                            )

                            db.execSQL(
                                """
                                    INSERT INTO reviews_table (stars, revText, userId, storeId) VALUES
                                    (5, 'Excellent service and great ambiance. Highly recommended!', 1, 1),
                                    (4, 'Good food but the wait time was a bit long. Would come back!', 2, 2),
                                    (3, 'Decent place, but the food was just average. Not bad, but not great either.', 3, 3),
                                    (2, 'The store was too crowded and the service was slow. Disappointing experience.', 4, 4);
                                """
                            )

                            db.execSQL(
                                """
                                    INSERT INTO slots_table (hour, availability, day, storeId) VALUES
                                    (2, 10, 'Monday', 1),
                                    (4, 10, 'Monday', 1),
                                    (6, 10, 'Tuesday', 1),
                                    (8, 10, 'Wednesday', 1),
                                    (10, 10, 'Thursday', 1),
                                    (2, 10, 'Friday', 2),
                                    (4, 10, 'Saturday', 2),
                                    (6, 10, 'Sunday', 2),
                                    (8, 10, 'Monday', 2),
                                    (10, 10, 'Tuesday', 2),
                                    (2, 10, 'Wednesday', 3),
                                    (4, 10, 'Thursday', 3),
                                    (6, 10, 'Friday', 3),
                                    (8, 10, 'Saturday', 3),
                                    (10, 10, 'Sunday', 3),
                                    (2, 10, 'Monday', 4),
                                    (4, 10, 'Tuesday', 4),
                                    (6, 10, 'Wednesday', 4),
                                    (8, 10, 'Thursday', 4),
                                    (10, 10, 'Friday', 4),
                                    (2, 10, 'Saturday', 4),
                                    (4, 10, 'Sunday', 1),
                                    (6, 10, 'Monday', 1),
                                    (8, 10, 'Tuesday', 2),
                                    (10, 10, 'Wednesday', 2),
                                    (2, 10, 'Thursday', 3),
                                    (4, 10, 'Friday', 3),
                                    (6, 10, 'Saturday', 4),
                                    (8, 10, 'Sunday', 4);
                                    """
                            )








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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }





}

