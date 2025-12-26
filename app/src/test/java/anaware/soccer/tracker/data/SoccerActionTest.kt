package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Unit tests for SoccerAction data class.
 */
class SoccerActionTest {

    @Test
    fun `constructor with current time returns valid SoccerAction`() {
        val now = LocalDateTime.now()
        val action = SoccerAction(
            id = 1,
            dateTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(5, action.actionCount)
        assertEquals("GOAL", action.actionType)
        assertTrue(action.isMatch)
        assertEquals("Team A", action.opponent)
        assertNotNull(action.dateTime)
    }

    @Test
    fun `constructor with custom datetime returns valid SoccerAction`() {
        val customDateTime = LocalDateTime.of(2025, 12, 19, 14, 30)
        val action = SoccerAction(
            id = 2,
            dateTime = customDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 3,
            actionType = ActionType.ASSIST.name,
            isMatch = false,
            opponent = "Team B"
        )

        assertEquals(3, action.actionCount)
        assertEquals("ASSIST", action.actionType)
        assertFalse(action.isMatch)
        assertEquals("Team B", action.opponent)
        assertEquals(
            customDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            action.dateTime
        )
    }

    @Test
    fun `constructor with empty opponent is valid`() {
        val action = SoccerAction(
            id = 3,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 2,
            actionType = ActionType.OFFENSIVE_ACTION.name,
            isMatch = false,
            opponent = ""
        )

        assertEquals("", action.opponent)
    }

    @Test
    fun `constructor with zero actions is valid`() {
        val action = SoccerAction(
            id = 4,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 0,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team C"
        )

        assertEquals(0, action.actionCount)
    }

    @Test
    fun `getActionTypeEnum returns correct ActionType`() {
        val goalAction = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(ActionType.GOAL, goalAction.getActionTypeEnum())
    }

    @Test
    fun `getActionTypeEnum returns default for invalid type`() {
        val invalidAction = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "INVALID_TYPE",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(ActionType.default(), invalidAction.getActionTypeEnum())
    }

    @Test
    fun `getLocalDateTime returns correct LocalDateTime`() {
        val expectedDateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action = SoccerAction(
            id = 1,
            dateTime = expectedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(expectedDateTime, action.getLocalDateTime())
    }

    @Test
    fun `getFormattedDate returns date in correct format`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        val formatted = action.getFormattedDate()
        // Check that it contains the date components (locale-independent)
        assertTrue(formatted.contains("19"))
        assertTrue(formatted.contains("2025"))
    }

    @Test
    fun `getFormattedTime returns correct format`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("15:45", action.getFormattedTime())
    }
}
