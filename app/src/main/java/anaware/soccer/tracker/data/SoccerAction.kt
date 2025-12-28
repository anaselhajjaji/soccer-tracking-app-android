package anaware.soccer.tracker.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class representing a soccer action entry.
 * Tracks offensive actions during matches or training sessions.
 */
data class SoccerAction(
    val id: Long = 0,

    val dateTime: String, // Stored as ISO-8601 string

    val actionCount: Int,

    val actionType: String, // Stored as string: GOAL, ASSIST, or OFFENSIVE_ACTION

    val isMatch: Boolean, // true for match, false for training

    val opponent: String = "", // Name of the opponent team or empty for training

    val playerId: String = "", // ID of the player who performed the action

    val teamId: String = "", // ID of the team the player was representing

    val matchId: String = "" // ID of the match this action belongs to (empty for training or legacy)
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

    /**
     * Returns true if this is a legacy action (no player assigned).
     */
    fun isLegacyAction(): Boolean {
        return playerId.isBlank()
    }
}
