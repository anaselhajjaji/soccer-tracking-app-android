package anaware.soccer.tracker.data

/**
 * Enum representing the type of soccer action.
 */
enum class ActionType {
    GOAL,
    ASSIST,
    OFFENSIVE_ACTION;

    /**
     * Returns a user-friendly display name for the action type.
     */
    fun displayName(): String = when (this) {
        GOAL -> "Goal"
        ASSIST -> "Assist"
        OFFENSIVE_ACTION -> "Offensive Action"
    }

    companion object {
        /**
         * Returns all action types as a list.
         */
        fun all(): List<ActionType> = listOf(GOAL, ASSIST, OFFENSIVE_ACTION)

        /**
         * Returns the default action type.
         */
        fun default(): ActionType = OFFENSIVE_ACTION
    }
}
