package com.example.room_setup_composables

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        // Add new columns to the "booking_table"
        db.execSQL("ALTER TABLE booking_table ADD COLUMN phoneNumber TEXT")
        db.execSQL("ALTER TABLE booking_table ADD COLUMN persons INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE booking_table ADD COLUMN occasion TEXT NOT NULL DEFAULT ''")

        // Add new column to the "store_table"
        db.execSQL("ALTER TABLE store_table ADD COLUMN availability INTEGER NOT NULL DEFAULT 0")

        // Add new columns to the "users_table"
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
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }





}

