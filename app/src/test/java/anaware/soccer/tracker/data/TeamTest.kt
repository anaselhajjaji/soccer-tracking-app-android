package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Team data class.
 */
class TeamTest {

    @Test
    fun `Team default constructor creates instance with default values`() {
        val team = Team()

        assertEquals("", team.id)
        assertEquals("", team.name)
        assertEquals("#2196F3", team.color)
        assertEquals("", team.league)
        assertEquals("", team.season)
    }

    @Test
    fun `Team constructor with all parameters creates instance correctly`() {
        val team = Team(
            id = "team-123",
            name = "Blue Thunder",
            color = "#FF0000",
            league = "Premier Youth League",
            season = "2024-2025"
        )

        assertEquals("team-123", team.id)
        assertEquals("Blue Thunder", team.name)
        assertEquals("#FF0000", team.color)
        assertEquals("Premier Youth League", team.league)
        assertEquals("2024-2025", team.season)
    }

    @Test
    fun `Team default color is blue when not specified`() {
        val team = Team(
            name = "Default Color Team"
        )

        assertEquals("#2196F3", team.color)
    }

    @Test
    fun `getDisplayName returns name with season when season is provided`() {
        val team = Team(
            name = "Blue Thunder",
            season = "2024-2025"
        )

        val displayName = team.getDisplayName()

        assertEquals("Blue Thunder (2024-2025)", displayName)
    }

    @Test
    fun `getDisplayName returns name only when season is blank`() {
        val team = Team(
            name = "Blue Thunder",
            season = ""
        )

        val displayName = team.getDisplayName()

        assertEquals("Blue Thunder", displayName)
    }

    @Test
    fun `getDisplayName handles various season formats`() {
        val testCases = listOf(
            Team(name = "Team A", season = "2024-2025") to "Team A (2024-2025)",
            Team(name = "Team B", season = "Fall 2024") to "Team B (Fall 2024)",
            Team(name = "Team C", season = "") to "Team C",
            Team(name = "Team D", season = "2024") to "Team D (2024)"
        )

        testCases.forEach { (team, expected) ->
            assertEquals(expected, team.getDisplayName())
        }
    }

    @Test
    fun `Team with league but no season displays correctly`() {
        val team = Team(
            name = "Thunder FC",
            league = "Premier Youth League",
            season = ""
        )

        assertEquals("Thunder FC", team.getDisplayName())
        assertEquals("Premier Youth League", team.league)
    }

    @Test
    fun `Team with season but no league displays correctly`() {
        val team = Team(
            name = "Thunder FC",
            league = "",
            season = "2024-2025"
        )

        assertEquals("Thunder FC (2024-2025)", team.getDisplayName())
    }

    @Test
    fun `Team supports standard hex color codes`() {
        val colors = listOf(
            "#FF0000", // Red
            "#00FF00", // Green
            "#0000FF", // Blue
            "#FFFF00", // Yellow
            "#FF00FF", // Magenta
            "#00FFFF", // Cyan
            "#FFFFFF", // White
            "#000000"  // Black
        )

        colors.forEach { color ->
            val team = Team(color = color)
            assertEquals(color, team.color)
        }
    }

    @Test
    fun `Team supports short hex color codes`() {
        val team = Team(color = "#FFF")

        assertEquals("#FFF", team.color)
    }

    @Test
    fun `Team copy creates new instance with updated fields`() {
        val original = Team(
            id = "team-1",
            name = "Original Team",
            color = "#FF0000",
            league = "League A",
            season = "2024"
        )

        val updated = original.copy(
            name = "Updated Team",
            color = "#00FF00"
        )

        assertEquals("team-1", updated.id)
        assertEquals("Updated Team", updated.name)
        assertEquals("#00FF00", updated.color)
        assertEquals("League A", updated.league)
        assertEquals("2024", updated.season)
        assertNotSame(original, updated)
    }

    @Test
    fun `Team equals compares by value not reference`() {
        val team1 = Team(
            id = "team-1",
            name = "Blue Thunder",
            color = "#2196F3",
            league = "Premier",
            season = "2024"
        )

        val team2 = Team(
            id = "team-1",
            name = "Blue Thunder",
            color = "#2196F3",
            league = "Premier",
            season = "2024"
        )

        assertEquals(team1, team2)
        assertNotSame(team1, team2)
    }

    @Test
    fun `Team hashCode is consistent for equal instances`() {
        val team1 = Team(
            id = "team-1",
            name = "Blue Thunder",
            color = "#2196F3",
            league = "Premier",
            season = "2024"
        )

        val team2 = Team(
            id = "team-1",
            name = "Blue Thunder",
            color = "#2196F3",
            league = "Premier",
            season = "2024"
        )

        assertEquals(team1.hashCode(), team2.hashCode())
    }

    @Test
    fun `Team toString returns readable string representation`() {
        val team = Team(
            id = "team-1",
            name = "Blue Thunder",
            color = "#2196F3",
            league = "Premier",
            season = "2024"
        )

        val stringRepresentation = team.toString()

        assertTrue(stringRepresentation.contains("Team"))
        assertTrue(stringRepresentation.contains("Blue Thunder"))
        assertTrue(stringRepresentation.contains("#2196F3"))
    }

    @Test
    fun `Team with special characters in name handles correctly`() {
        val team = Team(
            name = "São Paulo FC & Friends"
        )

        assertEquals("São Paulo FC & Friends", team.name)
        assertEquals("São Paulo FC & Friends", team.getDisplayName())
    }

    @Test
    fun `Team with emoji in name handles correctly`() {
        val team = Team(
            name = "Thunder ⚡ FC"
        )

        assertEquals("Thunder ⚡ FC", team.name)
        assertEquals("Thunder ⚡ FC", team.getDisplayName())
    }

    @Test
    fun `Team with long name handles correctly`() {
        val longName = "The Very Long Team Name Youth Soccer Club Premier Division"
        val team = Team(
            name = longName,
            season = "2024-2025"
        )

        assertEquals(longName, team.name)
        assertEquals("$longName (2024-2025)", team.getDisplayName())
    }

    @Test
    fun `Team with long season description handles correctly`() {
        val team = Team(
            name = "Thunder FC",
            season = "Fall 2024 - Spring 2025 Championship Season"
        )

        assertEquals("Thunder FC (Fall 2024 - Spring 2025 Championship Season)", team.getDisplayName())
    }

    @Test
    fun `Team different colors produce different hashCodes`() {
        val team1 = Team(id = "team-1", name = "Team", color = "#FF0000")
        val team2 = Team(id = "team-1", name = "Team", color = "#00FF00")

        assertNotEquals(team1.hashCode(), team2.hashCode())
    }

    @Test
    fun `Team with uppercase and lowercase color codes are different`() {
        val team1 = Team(color = "#FFFFFF")
        val team2 = Team(color = "#ffffff")

        // These should be different because hex codes are case-sensitive strings
        assertNotEquals(team1, team2)
    }

    @Test
    fun `Team with multiple seasons separated updates correctly`() {
        val original = Team(
            id = "team-1",
            name = "Thunder FC",
            season = "2023-2024"
        )

        val updated = original.copy(season = "2024-2025")

        assertEquals("Thunder FC (2023-2024)", original.getDisplayName())
        assertEquals("Thunder FC (2024-2025)", updated.getDisplayName())
    }

    @Test
    fun `Team with blank name still has valid display name`() {
        val team = Team(
            name = "",
            season = "2024"
        )

        assertEquals(" (2024)", team.getDisplayName())
    }

    @Test
    fun `Team league field stores various league names`() {
        val leagues = listOf(
            "Premier Youth League",
            "Division 1",
            "U12 Competitive",
            "Regional Championship",
            "Local Recreation League"
        )

        leagues.forEach { league ->
            val team = Team(league = league)
            assertEquals(league, team.league)
        }
    }

    @Test
    fun `Team season field stores various season formats`() {
        val seasons = listOf(
            "2024-2025",
            "Fall 2024",
            "Spring 2025",
            "2024",
            "Summer Tournament 2024"
        )

        seasons.forEach { season ->
            val team = Team(season = season)
            assertEquals(season, team.season)
        }
    }
}
