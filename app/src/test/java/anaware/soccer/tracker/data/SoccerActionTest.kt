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

    @Test
    fun `SoccerAction with different action types creates different instances`() {
        val goal = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        val assist = SoccerAction(
            id = 2,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 3,
            actionType = ActionType.ASSIST.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertNotEquals(goal.actionType, assist.actionType)
        assertEquals(ActionType.GOAL, goal.getActionTypeEnum())
        assertEquals(ActionType.ASSIST, assist.getActionTypeEnum())
    }

    @Test
    fun `SoccerAction equals compares by all fields`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action1 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        val action2 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(action1, action2)
    }

    @Test
    fun `SoccerAction hashCode is consistent for equal instances`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action1 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        val action2 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals(action1.hashCode(), action2.hashCode())
    }

    @Test
    fun `SoccerAction with large action count stores correctly`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 999,
            actionType = ActionType.OFFENSIVE_ACTION.name,
            isMatch = false,
            opponent = ""
        )

        assertEquals(999, action.actionCount)
    }

    @Test
    fun `SoccerAction with very long opponent name stores correctly`() {
        val longOpponentName = "A Very Long Opponent Team Name With Multiple Words And Characters"

        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = longOpponentName
        )

        assertEquals(longOpponentName, action.opponent)
        assertTrue(action.opponent.length > 50)
    }

    @Test
    fun `SoccerAction with special characters in opponent name stores correctly`() {
        val specialOpponent = "FC München (U-18)"

        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = specialOpponent
        )

        assertEquals(specialOpponent, action.opponent)
        assertTrue(action.opponent.contains("("))
        assertTrue(action.opponent.contains(")"))
    }

    @Test
    fun `getFormattedTime handles midnight correctly`() {
        val midnight = LocalDateTime.of(2025, 12, 19, 0, 0)
        val action = SoccerAction(
            id = 1,
            dateTime = midnight.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("00:00", action.getFormattedTime())
    }

    @Test
    fun `getFormattedTime handles end of day correctly`() {
        val endOfDay = LocalDateTime.of(2025, 12, 19, 23, 59)
        val action = SoccerAction(
            id = 1,
            dateTime = endOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("23:59", action.getFormattedTime())
    }

    @Test
    fun `SoccerAction toString includes all key information`() {
        val action = SoccerAction(
            id = 123,
            dateTime = "2025-12-19T15:45:00",
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        val stringRep = action.toString()

        assertTrue(stringRep.contains("123") || stringRep.contains("id"))
        assertTrue(stringRep.contains("GOAL") || stringRep.contains("actionType"))
    }
}
