package anaware.soccer.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for storing soccer action records.
 */
@Database(
    entities = [SoccerAction::class],
    version = 3,
    exportSchema = false
)
abstract class SoccerDatabase : RoomDatabase() {

    abstract fun soccerActionDao(): SoccerActionDao

    companion object {
        @Volatile
        private var INSTANCE: SoccerDatabase? = null

        /**
         * Gets the singleton instance of the database.
         * Thread-safe implementation using double-checked locking.
         */
        fun getDatabase(context: Context): SoccerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoccerDatabase::class.java,
                    "soccer_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
