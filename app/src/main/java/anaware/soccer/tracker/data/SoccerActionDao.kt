package anaware.soccer.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for soccer actions.
 * Provides methods to interact with the soccer_actions table.
 */
@Dao
interface SoccerActionDao {

    /**
     * Inserts a new soccer action record.
     * @return the ID of the inserted record
     */
    @Insert
    suspend fun insert(action: SoccerAction): Long

    /**
     * Deletes a specific soccer action record.
     */
    @Delete
    suspend fun delete(action: SoccerAction)

    /**
     * Deletes all soccer action records.
     */
    @Query("DELETE FROM soccer_actions")
    suspend fun deleteAll()

    /**
     * Returns all soccer actions ordered by date/time (newest first).
     */
    @Query("SELECT * FROM soccer_actions ORDER BY dateTime DESC")
    fun getAllActions(): Flow<List<SoccerAction>>

    /**
     * Returns the total count of all actions across all records.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions")
    fun getTotalActionCount(): Flow<Int?>

    /**
     * Returns actions grouped by date for charting purposes.
     * Returns list ordered by date ascending (oldest first) for chart display.
     */
    @Query("SELECT * FROM soccer_actions ORDER BY dateTime ASC")
    fun getActionsForChart(): Flow<List<SoccerAction>>

    /**
     * Returns actions filtered by action type, ordered by date ascending for chart display.
     */
    @Query("SELECT * FROM soccer_actions WHERE actionType = :actionType ORDER BY dateTime ASC")
    fun getActionsByType(actionType: String): Flow<List<SoccerAction>>

    /**
     * Returns the total count of actions for a specific action type.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions WHERE actionType = :actionType")
    fun getTotalCountByType(actionType: String): Flow<Int?>

    /**
     * Returns actions filtered by session type (match/training), ordered by date ascending for chart display.
     */
    @Query("SELECT * FROM soccer_actions WHERE isMatch = :isMatch ORDER BY dateTime ASC")
    fun getActionsBySessionType(isMatch: Boolean): Flow<List<SoccerAction>>

    /**
     * Returns the total count of actions for a specific session type.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions WHERE isMatch = :isMatch")
    fun getTotalCountBySessionType(isMatch: Boolean): Flow<Int?>

    /**
     * Returns actions filtered by both action type and session type, ordered by date ascending for chart display.
     */
    @Query("SELECT * FROM soccer_actions WHERE actionType = :actionType AND isMatch = :isMatch ORDER BY dateTime ASC")
    fun getActionsByTypeAndSessionType(actionType: String, isMatch: Boolean): Flow<List<SoccerAction>>

    /**
     * Returns the total count of actions for a specific action type and session type.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions WHERE actionType = :actionType AND isMatch = :isMatch")
    fun getTotalCountByTypeAndSessionType(actionType: String, isMatch: Boolean): Flow<Int?>

    /**
     * Returns all actions as a list (for backup operations).
     */
    @Query("SELECT * FROM soccer_actions ORDER BY dateTime DESC")
    suspend fun getAllActionsForBackup(): List<SoccerAction>

    /**
     * Returns a list of distinct opponent names (non-empty) for autocomplete.
     */
    @Query("SELECT DISTINCT opponent FROM soccer_actions WHERE opponent != '' ORDER BY opponent ASC")
    fun getDistinctOpponents(): Flow<List<String>>

    /**
     * Returns actions filtered by action type and opponent, ordered by date ascending for chart display.
     */
    @Query("SELECT * FROM soccer_actions WHERE actionType = :actionType AND opponent = :opponent ORDER BY dateTime ASC")
    fun getActionsByTypeAndOpponent(actionType: String, opponent: String): Flow<List<SoccerAction>>

    /**
     * Returns actions filtered by action type, session type, and opponent, ordered by date ascending for chart display.
     */
    @Query("SELECT * FROM soccer_actions WHERE actionType = :actionType AND isMatch = :isMatch AND opponent = :opponent ORDER BY dateTime ASC")
    fun getActionsByTypeSessionAndOpponent(actionType: String, isMatch: Boolean, opponent: String): Flow<List<SoccerAction>>

    /**
     * Returns the total count of actions for a specific action type and opponent.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions WHERE actionType = :actionType AND opponent = :opponent")
    fun getTotalCountByTypeAndOpponent(actionType: String, opponent: String): Flow<Int?>

    /**
     * Returns the total count of actions for a specific action type, session type, and opponent.
     */
    @Query("SELECT SUM(actionCount) FROM soccer_actions WHERE actionType = :actionType AND isMatch = :isMatch AND opponent = :opponent")
    fun getTotalCountByTypeSessionAndOpponent(actionType: String, isMatch: Boolean, opponent: String): Flow<Int?>
}
