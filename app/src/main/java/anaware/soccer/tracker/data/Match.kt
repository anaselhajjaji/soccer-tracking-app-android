package anaware.soccer.tracker.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Represents a soccer match between two teams.
 *
 * A match groups related actions together and stores metadata about the game including
 * date, teams, league/tournament, and scores.
 *
 * @property id Unique identifier (UUID)
 * @property date Match date in ISO format (yyyy-MM-dd)
 * @property playerTeamId Reference to the player's team (can be empty for legacy data)
 * @property opponentTeamId Reference to the opponent team
 * @property league Optional league or tournament name
 * @property playerScore Player team's score (-1 if not recorded)
 * @property opponentScore Opponent team's score (-1 if not recorded)
 * @property isHomeMatch True if home match, false if away match (default true)
 */
data class Match(
    val id: String = "",
    val date: String = "",
    val playerTeamId: String = "",
    val opponentTeamId: String = "",
    val league: String = "",
    val playerScore: Int = -1,
    val opponentScore: Int = -1,
    val isHomeMatch: Boolean = true
) {
    /**
     * Converts the ISO date string to LocalDate.
     */
    fun getLocalDate(): LocalDate {
        return LocalDate.parse(date)
    }

    /**
     * Returns the match date in a formatted string (e.g., "Dec 28, 2025").
     */
    fun getFormattedDate(): String {
        return getLocalDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    /**
     * Returns the score in display format (e.g., "3-2" or "Not recorded").
     */
    fun getScoreDisplay(): String {
        return if (hasScores()) {
            "$playerScore-$opponentScore"
        } else {
            "Not recorded"
        }
    }

    /**
     * Returns the match result (WIN/LOSS/DRAW) or null if scores not recorded.
     */
    fun getResult(): MatchResult? {
        return if (hasScores()) {
            when {
                playerScore > opponentScore -> MatchResult.WIN
                playerScore < opponentScore -> MatchResult.LOSS
                else -> MatchResult.DRAW
            }
        } else {
            null
        }
    }

    /**
     * Returns true if both scores have been recorded (>= 0).
     */
    fun hasScores(): Boolean {
        return playerScore >= 0 && opponentScore >= 0
    }
}

/**
 * Represents the result of a match from the player's team perspective.
 */
enum class MatchResult {
    WIN, LOSS, DRAW;

    /**
     * Returns the display name for this result.
     */
    fun displayName(): String {
        return when (this) {
            WIN -> "Win"
            LOSS -> "Loss"
            DRAW -> "Draw"
        }
    }
}
