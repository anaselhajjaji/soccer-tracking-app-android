package anaware.soccer.tracker

import android.app.Application
import anaware.soccer.tracker.data.SoccerDatabase
import anaware.soccer.tracker.data.SoccerRepository

/**
 * Application class for initializing app-wide dependencies.
 */
class SoccerTrackerApp : Application() {

    /**
     * Database instance - lazily initialized.
     */
    private val database by lazy { SoccerDatabase.getDatabase(this) }

    /**
     * Repository instance - lazily initialized.
     */
    val repository by lazy { SoccerRepository(database.soccerActionDao()) }
}
