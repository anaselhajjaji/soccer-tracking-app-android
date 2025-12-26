package anaware.soccer.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Entity representing a soccer action entry.
 * Tracks offensive actions during matches or training sessions.
 */
@Entity(tableName = "soccer_actions")
data class SoccerAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val dateTime: String, // Stored as ISO-8601 string for Room compatibility

    val actionCount: Int,

    val actionType: String, // Stored as string: GOAL, ASSIST, or OFFENSIVE_ACTION

    val isMatch: Boolean, // true for match, false for training

    val opponent: String = "" // Name of the opponent team or empty for training
) {
    /**
     * Returns the action type as an ActionType enum.
     */
    fun getActionTypeEnum(): ActionType {
        return try {
            ActionType.valueOf(actionType)
        } catch (e: IllegalArgumentException) {
            ActionType.default()
        }
    }
    /**
     * Returns the datetime as a LocalDateTime object.
     */
    fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    /**
     * Returns a formatted date string for display (e.g., "Dec 18, 2025").
     */
    fun getFormattedDate(): String {
        val dateTime = getLocalDateTime()
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    /**
     * Returns a formatted time string for display (e.g., "14:30").
     */
    fun getFormattedTime(): String {
        val dateTime = getLocalDateTime()
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    companion object {
        /**
         * Creates a SoccerAction with the current date/time.
         */
        fun create(
            actionCount: Int,
            actionType: ActionType,
            isMatch: Boolean,
            opponent: String = ""
        ): SoccerAction {
            val now = LocalDateTime.now()
            return SoccerAction(
                dateTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                actionCount = actionCount,
                actionType = actionType.name,
                isMatch = isMatch,
                opponent = opponent
            )
        }

        /**
         * Creates a SoccerAction with a custom date/time.
         */
        fun create(
            actionCount: Int,
            actionType: ActionType,
            isMatch: Boolean,
            dateTime: LocalDateTime,
            opponent: String = ""
        ): SoccerAction {
            return SoccerAction(
                dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                actionCount = actionCount,
                actionType = actionType.name,
                isMatch = isMatch,
                opponent = opponent
            )
        }
    }
}
