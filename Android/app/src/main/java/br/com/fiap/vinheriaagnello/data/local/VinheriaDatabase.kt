package br.com.fiap.vinheriaagnello.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database that owns the local wine inventory schema.
 *
 * Schema exports are enabled so database changes can be reviewed and migration-tested.
 */
@Database(
    entities = [WineEntity::class],
    version = 2,
    exportSchema = true
)
abstract class VinheriaDatabase : RoomDatabase() {
    /** Provides access to wine persistence operations. */
    abstract fun wineDao(): WineDao

    companion object {
        private const val DATABASE_NAME = "vinheria_stock.db"

        // Volatile visibility plus synchronized initialization guarantees one process-wide instance.
        @Volatile
        private var instance: VinheriaDatabase? = null

        /**
         * Returns the process-wide database instance using the application context to avoid leaks.
         */
        fun getInstance(context: Context): VinheriaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    VinheriaDatabase::class.java,
                    DATABASE_NAME
                )
                    // This academic version intentionally recreates data when the schema is incompatible.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instance = it }
            }
    }
}
