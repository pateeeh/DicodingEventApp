package com.example.ujiandicoding.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Events::class, Setting::class], version = 3, exportSchema = false)
abstract class EventsRoomDatabase : RoomDatabase() {
    abstract fun eventsDao(): EventsDao

    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: EventsRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): EventsRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventsRoomDatabase::class.java, "events_database"
                )
                    .addMigrations(MIGRATION_1_2) // Tambahkan migrasi
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migrasi dari versi 1 ke 2 (Menambahkan kolom baru)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Events ADD COLUMN summary TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE Events ADD COLUMN ownerName TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE Events ADD COLUMN mediaCover TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE Events ADD COLUMN imageLogo TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS settings (
                id INTEGER PRIMARY KEY NOT NULL,
                notificationsEnabled INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
            }
        }
    }
}
