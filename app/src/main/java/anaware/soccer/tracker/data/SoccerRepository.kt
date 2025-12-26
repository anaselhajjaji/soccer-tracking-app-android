package anaware.soccer.tracker.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing soccer action data.
 * Provides a clean API for the UI layer to interact with the database.
 */
class SoccerRepository(private val soccerActionDao: SoccerActionDao) {

    /**
     * Returns all soccer actions as a Flow (automatically updates when data changes).
     */
    val allActions: Flow<List<SoccerAction>> = soccerActionDao.getAllActions()

    /**
     * Returns actions for chart display (ordered chronologically).
     */
    val chartActions: Flow<List<SoccerAction>> = soccerActionDao.getActionsForChart()

    /**
     * Returns the total count of all actions.
     */
    val totalActionCount: Flow<Int?> = soccerActionDao.getTotalActionCount()

    /**
     * Inserts a new soccer action record.
     */
    suspend fun insertAction(action: SoccerAction): Long {
        return soccerActionDao.insert(action)
    }

    /**
     * Deletes a specific soccer action record.
     */
    suspend fun deleteAction(action: SoccerAction) {
        soccerActionDao.delete(action)
    }

    /**
     * Returns actions filtered by action type for chart display.
     */
    fun getActionsByType(actionType: ActionType): Flow<List<SoccerAction>> {
        return soccerActionDao.getActionsByType(actionType.name)
    }

    /**
     * Returns the total count for a specific action type.
     */
    fun getTotalCountByType(actionType: ActionType): Flow<Int?> {
        return soccerActionDao.getTotalCountByType(actionType.name)
    }

    /**
     * Returns actions filtered by session type (match/training).
     */
    fun getActionsBySessionType(isMatch: Boolean): Flow<List<SoccerAction>> {
        return soccerActionDao.getActionsBySessionType(isMatch)
    }

    /**
     * Returns the total count for a specific session type.
     */
    fun getTotalCountBySessionType(isMatch: Boolean): Flow<Int?> {
        return soccerActionDao.getTotalCountBySessionType(isMatch)
    }

    /**
     * Returns actions filtered by both action type and session type.
     */
    fun getActionsByTypeAndSessionType(actionType: ActionType, isMatch: Boolean): Flow<List<SoccerAction>> {
        return soccerActionDao.getActionsByTypeAndSessionType(actionType.name, isMatch)
    }

    /**
     * Returns the total count for a specific action type and session type combination.
     */
    fun getTotalCountByTypeAndSessionType(actionType: ActionType, isMatch: Boolean): Flow<Int?> {
        return soccerActionDao.getTotalCountByTypeAndSessionType(actionType.name, isMatch)
    }

    /**
     * Gets all actions as a list (for backup).
     */
    suspend fun getAllActionsForBackup(): List<SoccerAction> {
        return soccerActionDao.getAllActionsForBackup()
    }

    /**
     * Inserts multiple actions (for restore).
     */
    suspend fun insertActions(actions: List<SoccerAction>) {
        actions.forEach { soccerActionDao.insert(it) }
    }

    /**
     * Deletes all actions (for restore - to clear before importing).
     */
    suspend fun deleteAllActions() {
        soccerActionDao.deleteAll()
    }

    /**
     * Returns a list of distinct opponent names for autocomplete.
     */
    val distinctOpponents: Flow<List<String>> = soccerActionDao.getDistinctOpponents()

    /**
     * Returns actions filtered by action type and opponent.
     */
    fun getActionsByTypeAndOpponent(actionType: ActionType, opponent: String): Flow<List<SoccerAction>> {
        return soccerActionDao.getActionsByTypeAndOpponent(actionType.name, opponent)
    }

    /**
     * Returns actions filtered by action type, session type, and opponent.
     */
    fun getActionsByTypeSessionAndOpponent(actionType: ActionType, isMatch: Boolean, opponent: String): Flow<List<SoccerAction>> {
        return soccerActionDao.getActionsByTypeSessionAndOpponent(actionType.name, isMatch, opponent)
    }

    /**
     * Returns the total count for a specific action type and opponent.
     */
    fun getTotalCountByTypeAndOpponent(actionType: ActionType, opponent: String): Flow<Int?> {
        return soccerActionDao.getTotalCountByTypeAndOpponent(actionType.name, opponent)
    }

    /**
     * Returns the total count for a specific action type, session type, and opponent.
     */
    fun getTotalCountByTypeSessionAndOpponent(actionType: ActionType, isMatch: Boolean, opponent: String): Flow<Int?> {
        return soccerActionDao.getTotalCountByTypeSessionAndOpponent(actionType.name, isMatch, opponent)
    }
}
