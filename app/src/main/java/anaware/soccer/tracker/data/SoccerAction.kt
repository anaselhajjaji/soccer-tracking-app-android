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

    companion object {
        /**
         * Calculate play time from PLAYER_IN/PLAYER_OUT actions.
         * Uses a state machine algorithm to pair IN/OUT actions chronologically.
         *
         * @param actions List of SoccerAction entries (should be filtered to single player if needed)
         * @return Total play time in minutes, or null if no valid pairs found
         *
         * Algorithm:
         * - Sort actions chronologically
         * - Pair each PLAYER_IN with the next PLAYER_OUT
         * - Ignore unpaired actions (standalone IN or OUT)
         * - Handle multiple IN/OUT pairs within same session
         * - Ignore consecutive IN actions (take first, ignore rest until OUT)
         */
        fun calculatePlayTime(actions: List<SoccerAction>): Int? {
            // Sort actions by time
            val sortedActions = actions.sortedBy { it.getLocalDateTime() }

            var totalMinutes = 0
            var inTime: LocalDateTime? = null

            sortedActions.forEach { action ->
                when (action.getActionTypeEnum()) {
                    ActionType.PLAYER_IN -> {
                        // If already in, ignore (invalid state - player can't be in twice)
                        if (inTime == null) {
                            inTime = action.getLocalDateTime()
                        }
                    }
                    ActionType.PLAYER_OUT -> {
                        // If we have a matching IN, calculate duration
                        inTime?.let { start ->
                            val end = action.getLocalDateTime()
                            val duration = java.time.Duration.between(start, end)
                            totalMinutes += duration.toMinutes().toInt()
                            inTime = null // Reset for next pair
                        }
                        // If no matching IN, ignore (unpaired OUT)
                    }
                    else -> {} // Ignore other action types
                }
            }

            return if (totalMinutes > 0) totalMinutes else null
        }
    }
}
