package anaware.soccer.tracker.data

import kotlinx.serialization.Serializable

/**
 * Data class for backup/restore operations.
 * Contains all soccer action records in a serializable format.
 */
@Serializable
data class BackupData(
    val version: Int = 2,
    val exportDate: String,
    val actions: List<BackupAction>
) {
    companion object {
        const val CURRENT_VERSION = 2
    }
}

/**
 * Serializable representation of a SoccerAction for backup.
 * Must have no-arg constructor for Firestore deserialization.
 */
@Serializable
data class BackupAction(
    val dateTime: String = "",
    val actionCount: Int = 0,
    val actionType: String = "",
    val match: Boolean = false,  // Firestore uses "match" not "isMatch"
    val opponent: String = ""
) {
    /**
     * Converts this backup action to a SoccerAction entity.
     * @param id The document ID from Firestore (should be the timestamp)
     */
    fun toSoccerAction(id: Long): SoccerAction {
        return SoccerAction(
            id = id,
            dateTime = dateTime,
            actionCount = actionCount,
            actionType = actionType,
            isMatch = match,  // Convert from Firestore "match" to "isMatch"
            opponent = opponent
        )
    }

    companion object {
        /**
         * Creates a BackupAction from a SoccerAction entity.
         */
        fun fromSoccerAction(action: SoccerAction): BackupAction {
            return BackupAction(
                dateTime = action.dateTime,
                actionCount = action.actionCount,
                actionType = action.actionType,
                match = action.isMatch,  // Convert from "isMatch" to Firestore "match"
                opponent = action.opponent
            )
        }
    }
}
