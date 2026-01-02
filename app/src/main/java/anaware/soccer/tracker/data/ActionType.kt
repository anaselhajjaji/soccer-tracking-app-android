package anaware.soccer.tracker.data

/**
 * Enum representing the type of soccer action.
 */
enum class ActionType {
    GOAL,
    ASSIST,
    OFFENSIVE_ACTION,
    DUEL_WIN,
    PLAYER_IN,
    PLAYER_OUT;

    /**
     * Returns a user-friendly display name for the action type.
     */
    fun displayName(): String = when (this) {
        GOAL -> "Goal"
        ASSIST -> "Assist"
        OFFENSIVE_ACTION -> "Offensive Action"
        DUEL_WIN -> "Duel Win"
        PLAYER_IN -> "Player In"
        PLAYER_OUT -> "Player Out"
    }

    /**
     * Returns true if this is a time-tracking action (IN/OUT).
     */
    fun isTimeTracking(): Boolean = when (this) {
        PLAYER_IN, PLAYER_OUT -> true
        else -> false
    }

    companion object {
        /**
         * Returns all action types as a list.
         */
        fun all(): List<ActionType> = listOf(GOAL, ASSIST, OFFENSIVE_ACTION, DUEL_WIN, PLAYER_IN, PLAYER_OUT)

        /**
         * Returns scoring action types (for filtering).
         */
        fun scoringActions(): List<ActionType> = listOf(GOAL, ASSIST, OFFENSIVE_ACTION, DUEL_WIN)

        /**
         * Returns time-tracking action types.
         */
        fun timeTrackingActions(): List<ActionType> = listOf(PLAYER_IN, PLAYER_OUT)

        /**
         * Returns the default action type.
         */
        fun default(): ActionType = OFFENSIVE_ACTION
    }
}
