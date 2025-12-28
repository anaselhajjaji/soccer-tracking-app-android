package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Unit tests for Match data class.
 */
class MatchTest {

    @Test
    fun `Match default constructor creates instance with default values`() {
        val match = Match()

        assertEquals("", match.id)
        assertEquals("", match.date)
        assertEquals("", match.playerTeamId)
        assertEquals("", match.opponentTeamId)
        assertEquals("", match.league)
        assertEquals(-1, match.playerScore)
        assertEquals(-1, match.opponentScore)
    }

    @Test
    fun `Match constructor with all parameters creates instance correctly`() {
        val match = Match(
            id = "match-123",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier League",
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals("match-123", match.id)
        assertEquals("2025-12-28", match.date)
        assertEquals("team-1", match.playerTeamId)
        assertEquals("team-2", match.opponentTeamId)
        assertEquals("Premier League", match.league)
        assertEquals(3, match.playerScore)
        assertEquals(2, match.opponentScore)
    }

    @Test
    fun `getLocalDate converts ISO string to LocalDate`() {
        val match = Match(date = "2025-12-28")

        val localDate = match.getLocalDate()

        assertEquals(2025, localDate.year)
        assertEquals(12, localDate.monthValue)
        assertEquals(28, localDate.dayOfMonth)
    }

    @Test
    fun `getFormattedDate returns correctly formatted date`() {
        val match = Match(date = "2025-12-28")

        val formattedDate = match.getFormattedDate()

        // Check that it contains the date components (locale-independent)
        assertTrue(formattedDate.contains("28"))
        assertTrue(formattedDate.contains("2025"))
        assertTrue(formattedDate.contains("Dec") || formattedDate.contains("12"))
    }

    @Test
    fun `getFormattedDate handles different months correctly`() {
        val match1 = Match(date = "2025-01-15")
        val match2 = Match(date = "2025-06-20")
        val match3 = Match(date = "2025-12-31")

        val formatted1 = match1.getFormattedDate()
        val formatted2 = match2.getFormattedDate()
        val formatted3 = match3.getFormattedDate()

        // Check that each contains the day and year (locale-independent)
        assertTrue(formatted1.contains("15") && formatted1.contains("2025"))
        assertTrue(formatted2.contains("20") && formatted2.contains("2025"))
        assertTrue(formatted3.contains("31") && formatted3.contains("2025"))
    }

    @Test
    fun `hasScores returns false when scores not recorded`() {
        val match = Match(
            playerScore = -1,
            opponentScore = -1
        )

        assertFalse(match.hasScores())
    }

    @Test
    fun `hasScores returns false when only one score recorded`() {
        val match1 = Match(playerScore = 3, opponentScore = -1)
        val match2 = Match(playerScore = -1, opponentScore = 2)

        assertFalse(match1.hasScores())
        assertFalse(match2.hasScores())
    }

    @Test
    fun `hasScores returns true when both scores recorded`() {
        val match = Match(
            playerScore = 3,
            opponentScore = 2
        )

        assertTrue(match.hasScores())
    }

    @Test
    fun `hasScores returns true for 0-0 score`() {
        val match = Match(
            playerScore = 0,
            opponentScore = 0
        )

        assertTrue(match.hasScores())
    }

    @Test
    fun `getScoreDisplay returns score when recorded`() {
        val match = Match(
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals("3-2", match.getScoreDisplay())
    }

    @Test
    fun `getScoreDisplay returns Not recorded when scores missing`() {
        val match = Match(
            playerScore = -1,
            opponentScore = -1
        )

        assertEquals("Not recorded", match.getScoreDisplay())
    }

    @Test
    fun `getScoreDisplay handles various score combinations`() {
        val testCases = listOf(
            Match(playerScore = 0, opponentScore = 0) to "0-0",
            Match(playerScore = 5, opponentScore = 3) to "5-3",
            Match(playerScore = 1, opponentScore = 1) to "1-1",
            Match(playerScore = -1, opponentScore = 2) to "Not recorded"
        )

        testCases.forEach { (match, expected) ->
            assertEquals(expected, match.getScoreDisplay())
        }
    }

    @Test
    fun `getResult returns WIN when player score higher`() {
        val match = Match(
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals(MatchResult.WIN, match.getResult())
    }

    @Test
    fun `getResult returns LOSS when opponent score higher`() {
        val match = Match(
            playerScore = 1,
            opponentScore = 3
        )

        assertEquals(MatchResult.LOSS, match.getResult())
    }

    @Test
    fun `getResult returns DRAW when scores equal`() {
        val match = Match(
            playerScore = 2,
            opponentScore = 2
        )

        assertEquals(MatchResult.DRAW, match.getResult())
    }

    @Test
    fun `getResult returns null when scores not recorded`() {
        val match = Match(
            playerScore = -1,
            opponentScore = -1
        )

        assertNull(match.getResult())
    }

    @Test
    fun `getResult handles 0-0 draw correctly`() {
        val match = Match(
            playerScore = 0,
            opponentScore = 0
        )

        assertEquals(MatchResult.DRAW, match.getResult())
    }

    @Test
    fun `MatchResult displayName returns correct strings`() {
        assertEquals("Win", MatchResult.WIN.displayName())
        assertEquals("Loss", MatchResult.LOSS.displayName())
        assertEquals("Draw", MatchResult.DRAW.displayName())
    }

    @Test
    fun `Match with empty playerTeamId is valid for legacy data`() {
        val match = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "",
            opponentTeamId = "team-2"
        )

        assertEquals("", match.playerTeamId)
        assertEquals("team-2", match.opponentTeamId)
    }

    @Test
    fun `Match with league stores value correctly`() {
        val match = Match(
            league = "Premier Youth League"
        )

        assertEquals("Premier Youth League", match.league)
    }

    @Test
    fun `Match equals compares by value not reference`() {
        val match1 = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        val match2 = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals(match1, match2)
        assertNotSame(match1, match2)
    }

    @Test
    fun `Match hashCode is consistent for equal instances`() {
        val match1 = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        val match2 = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals(match1.hashCode(), match2.hashCode())
    }

    @Test
    fun `Match copy creates new instance with updated fields`() {
        val original = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = -1,
            opponentScore = -1
        )

        val updated = original.copy(
            playerScore = 3,
            opponentScore = 2
        )

        assertEquals("match-1", updated.id)
        assertEquals("2025-12-28", updated.date)
        assertEquals(3, updated.playerScore)
        assertEquals(2, updated.opponentScore)
        assertNotSame(original, updated)
    }

    @Test
    fun `Match with different dates are not equal`() {
        val match1 = Match(id = "match-1", date = "2025-12-28")
        val match2 = Match(id = "match-1", date = "2025-12-29")

        assertNotEquals(match1, match2)
    }

    @Test
    fun `Match with different teams are not equal`() {
        val match1 = Match(id = "match-1", playerTeamId = "team-1", opponentTeamId = "team-2")
        val match2 = Match(id = "match-1", playerTeamId = "team-1", opponentTeamId = "team-3")

        assertNotEquals(match1, match2)
    }

    @Test
    fun `Match toString returns readable string representation`() {
        val match = Match(
            id = "match-1",
            date = "2025-12-28",
            playerTeamId = "team-1",
            opponentTeamId = "team-2",
            league = "Premier",
            playerScore = 3,
            opponentScore = 2
        )

        val stringRepresentation = match.toString()

        assertTrue(stringRepresentation.contains("Match"))
        assertTrue(stringRepresentation.contains("match-1"))
        assertTrue(stringRepresentation.contains("2025-12-28"))
    }
}
