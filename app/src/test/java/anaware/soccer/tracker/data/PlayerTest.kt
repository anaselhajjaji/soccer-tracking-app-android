package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Unit tests for Player data class.
 */
class PlayerTest {

    @Test
    fun `Player default constructor creates instance with empty values`() {
        val player = Player()

        assertEquals("", player.id)
        assertEquals("", player.name)
        assertEquals("", player.birthdate)
        assertEquals(0, player.number)
        assertTrue(player.teams.isEmpty())
    }

    @Test
    fun `Player constructor with all parameters creates instance correctly`() {
        val player = Player(
            id = "player-123",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1", "team-2")
        )

        assertEquals("player-123", player.id)
        assertEquals("John Doe", player.name)
        assertEquals("2010-05-15", player.birthdate)
        assertEquals(10, player.number)
        assertEquals(2, player.teams.size)
        assertEquals("team-1", player.teams[0])
        assertEquals("team-2", player.teams[1])
    }

    @Test
    fun `getBirthdate converts ISO string to LocalDate`() {
        val player = Player(
            birthdate = "2010-05-15"
        )

        val birthdate = player.getBirthdate()

        assertEquals(2010, birthdate.year)
        assertEquals(5, birthdate.monthValue)
        assertEquals(15, birthdate.dayOfMonth)
    }

    @Test
    fun `getFormattedBirthdate returns correctly formatted date`() {
        val player = Player(
            birthdate = "2010-05-15"
        )

        val formattedDate = player.getFormattedBirthdate()

        assertEquals("May 15, 2010", formattedDate)
    }

    @Test
    fun `getFormattedBirthdate handles different months correctly`() {
        val players = listOf(
            Player(birthdate = "2010-01-15") to "Jan 15, 2010",
            Player(birthdate = "2010-12-25") to "Dec 25, 2010",
            Player(birthdate = "2015-07-04") to "Jul 04, 2015"
        )

        players.forEach { (player, expected) ->
            assertEquals(expected, player.getFormattedBirthdate())
        }
    }

    @Test
    fun `getAge calculates age correctly`() {
        // Create a player born 10 years ago today
        val tenYearsAgo = LocalDate.now().minusYears(10)
        val birthdateString = tenYearsAgo.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val player = Player(birthdate = birthdateString)

        val age = player.getAge()

        assertEquals(10, age)
    }

    @Test
    fun `getAge calculates age correctly for player born yesterday last year`() {
        // Create a player who will turn 1 year old tomorrow
        val almostOneYearAgo = LocalDate.now().minusYears(1).plusDays(1)
        val birthdateString = almostOneYearAgo.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val player = Player(birthdate = birthdateString)

        val age = player.getAge()

        assertEquals(0, age) // Not yet 1 year old
    }

    @Test
    fun `getAge calculates age correctly for recent birthday`() {
        // Create a player who turned 5 years old yesterday
        val fiveYearsAndOneDayAgo = LocalDate.now().minusYears(5).minusDays(1)
        val birthdateString = fiveYearsAndOneDayAgo.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val player = Player(birthdate = birthdateString)

        val age = player.getAge()

        assertEquals(5, age)
    }

    @Test
    fun `getDisplayName returns name with number when number is positive`() {
        val player = Player(
            name = "John Doe",
            number = 10
        )

        val displayName = player.getDisplayName()

        assertEquals("John Doe #10", displayName)
    }

    @Test
    fun `getDisplayName returns name only when number is zero`() {
        val player = Player(
            name = "John Doe",
            number = 0
        )

        val displayName = player.getDisplayName()

        assertEquals("John Doe", displayName)
    }

    @Test
    fun `getDisplayName handles various jersey numbers`() {
        val testCases = listOf(
            Player(name = "Player 1", number = 1) to "Player 1 #1",
            Player(name = "Player 99", number = 99) to "Player 99 #99",
            Player(name = "Player 0", number = 0) to "Player 0"
        )

        testCases.forEach { (player, expected) ->
            assertEquals(expected, player.getDisplayName())
        }
    }

    @Test
    fun `Player with multiple teams stores all team IDs`() {
        val teamIds = listOf("team-1", "team-2", "team-3", "team-4")
        val player = Player(
            name = "Multi-Team Player",
            teams = teamIds
        )

        assertEquals(4, player.teams.size)
        assertTrue(player.teams.containsAll(teamIds))
    }

    @Test
    fun `Player with no teams has empty teams list`() {
        val player = Player(
            name = "No Team Player",
            teams = emptyList()
        )

        assertTrue(player.teams.isEmpty())
    }

    @Test
    fun `Player copy creates new instance with updated fields`() {
        val original = Player(
            id = "player-1",
            name = "Original Name",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        val updated = original.copy(
            name = "Updated Name",
            number = 20
        )

        assertEquals("player-1", updated.id)
        assertEquals("Updated Name", updated.name)
        assertEquals("2010-05-15", updated.birthdate)
        assertEquals(20, updated.number)
        assertEquals(listOf("team-1"), updated.teams)
        assertNotSame(original, updated)
    }

    @Test
    fun `Player equals compares by value not reference`() {
        val player1 = Player(
            id = "player-1",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        val player2 = Player(
            id = "player-1",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        assertEquals(player1, player2)
        assertNotSame(player1, player2)
    }

    @Test
    fun `Player hashCode is consistent for equal instances`() {
        val player1 = Player(
            id = "player-1",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        val player2 = Player(
            id = "player-1",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        assertEquals(player1.hashCode(), player2.hashCode())
    }

    @Test
    fun `Player toString returns readable string representation`() {
        val player = Player(
            id = "player-1",
            name = "John Doe",
            birthdate = "2010-05-15",
            number = 10,
            teams = listOf("team-1")
        )

        val stringRepresentation = player.toString()

        assertTrue(stringRepresentation.contains("Player"))
        assertTrue(stringRepresentation.contains("John Doe"))
        assertTrue(stringRepresentation.contains("10"))
    }

    @Test
    fun `Player with special characters in name handles correctly`() {
        val player = Player(
            name = "José María O'Brien-Smith"
        )

        assertEquals("José María O'Brien-Smith", player.name)
        assertEquals("José María O'Brien-Smith", player.getDisplayName())
    }

    @Test
    fun `Player with very high jersey number handles correctly`() {
        val player = Player(
            name = "High Number Player",
            number = 999
        )

        assertEquals(999, player.number)
        assertEquals("High Number Player #999", player.getDisplayName())
    }

    @Test
    fun `Player birthdate parsing handles leap year correctly`() {
        val player = Player(
            birthdate = "2020-02-29" // Leap year
        )

        val birthdate = player.getBirthdate()

        assertEquals(2020, birthdate.year)
        assertEquals(2, birthdate.monthValue)
        assertEquals(29, birthdate.dayOfMonth)
    }
}
