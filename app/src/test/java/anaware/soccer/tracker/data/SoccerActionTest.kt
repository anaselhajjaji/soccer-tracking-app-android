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

    // Backward compatibility tests for player and team fields

    @Test
    fun `constructor with empty playerId and teamId creates legacy action`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "",
            teamId = ""
        )

        assertEquals("", action.playerId)
        assertEquals("", action.teamId)
        assertTrue(action.isLegacyAction())
    }

    @Test
    fun `constructor with playerId and teamId creates non-legacy action`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        assertEquals("player-123", action.playerId)
        assertEquals("team-456", action.teamId)
        assertFalse(action.isLegacyAction())
    }

    @Test
    fun `constructor without player and team parameters defaults to legacy action`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("", action.playerId)
        assertEquals("", action.teamId)
        assertTrue(action.isLegacyAction())
    }

    @Test
    fun `isLegacyAction returns true for blank playerId`() {
        val action1 = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "",
            teamId = "team-456"
        )

        assertTrue(action1.isLegacyAction())

        val action2 = SoccerAction(
            id = 2,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "   ",
            teamId = "team-456"
        )

        assertTrue(action2.isLegacyAction())
    }

    @Test
    fun `isLegacyAction returns false for non-blank playerId`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = ""
        )

        assertFalse(action.isLegacyAction())
    }

    @Test
    fun `SoccerAction with only playerId is valid`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = ""
        )

        assertEquals("player-123", action.playerId)
        assertEquals("", action.teamId)
        assertFalse(action.isLegacyAction())
    }

    @Test
    fun `SoccerAction with only teamId but no playerId is legacy`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "",
            teamId = "team-456"
        )

        assertEquals("", action.playerId)
        assertEquals("team-456", action.teamId)
        assertTrue(action.isLegacyAction())
    }

    @Test
    fun `SoccerAction equals considers playerId and teamId`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action1 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        val action2 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        val action3 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-999",
            teamId = "team-456"
        )

        assertEquals(action1, action2)
        assertNotEquals(action1, action3)
    }

    @Test
    fun `SoccerAction copy preserves playerId and teamId`() {
        val original = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456"
        )

        val copied = original.copy(actionCount = 10)

        assertEquals("player-123", copied.playerId)
        assertEquals("team-456", copied.teamId)
        assertEquals(10, copied.actionCount)
        assertFalse(copied.isLegacyAction())
    }

    @Test
    fun `Legacy action can be upgraded with player and team`() {
        val legacy = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertTrue(legacy.isLegacyAction())

        val upgraded = legacy.copy(
            playerId = "player-123",
            teamId = "team-456"
        )

        assertFalse(upgraded.isLegacyAction())
        assertEquals("player-123", upgraded.playerId)
        assertEquals("team-456", upgraded.teamId)
    }

    // Match entity tests

    @Test
    fun `constructor without matchId creates action with empty matchId`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("", action.matchId)
    }

    @Test
    fun `constructor with matchId creates action correctly`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            matchId = "match-123"
        )

        assertEquals("match-123", action.matchId)
    }

    @Test
    fun `training action can have empty matchId`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = false,
            opponent = "",
            matchId = ""
        )

        assertEquals("", action.matchId)
        assertFalse(action.isMatch)
    }

    @Test
    fun `training action can have matchId if manually assigned`() {
        val action = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = false,
            opponent = "",
            matchId = "match-123"
        )

        assertEquals("match-123", action.matchId)
        assertFalse(action.isMatch)
    }

    @Test
    fun `SoccerAction equals considers matchId`() {
        val dateTime = LocalDateTime.of(2025, 12, 19, 15, 45)
        val action1 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456",
            matchId = "match-1"
        )

        val action2 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456",
            matchId = "match-1"
        )

        val action3 = SoccerAction(
            id = 1,
            dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456",
            matchId = "match-2"
        )

        assertEquals(action1, action2)
        assertNotEquals(action1, action3)
    }

    @Test
    fun `SoccerAction copy preserves matchId`() {
        val original = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A",
            playerId = "player-123",
            teamId = "team-456",
            matchId = "match-123"
        )

        val copied = original.copy(actionCount = 10)

        assertEquals("match-123", copied.matchId)
        assertEquals(10, copied.actionCount)
    }

    @Test
    fun `Legacy match action can be upgraded with matchId`() {
        val legacy = SoccerAction(
            id = 1,
            dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            actionCount = 5,
            actionType = ActionType.GOAL.name,
            isMatch = true,
            opponent = "Team A"
        )

        assertEquals("", legacy.matchId)

        val upgraded = legacy.copy(matchId = "match-123")

        assertEquals("match-123", upgraded.matchId)
    }

    // Play Time Calculation Tests

    @Test
    fun `calculatePlayTime returns null when no IN OUT actions`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 1,
                actionType = "GOAL",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertNull(playTime)
    }

    @Test
    fun `calculatePlayTime returns null for empty actions list`() {
        val playTime = SoccerAction.calculatePlayTime(emptyList())

        assertNull(playTime)
    }

    @Test
    fun `calculatePlayTime calculates single IN OUT pair correctly`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertEquals(30, playTime)
    }

    @Test
    fun `calculatePlayTime ignores unpaired IN action`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertNull(playTime)
    }

    @Test
    fun `calculatePlayTime ignores unpaired OUT action`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertNull(playTime)
    }

    @Test
    fun `calculatePlayTime handles multiple IN OUT pairs`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:20:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:40:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 4,
                dateTime = "2025-12-28T15:00:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // 20 minutes (first pair) + 20 minutes (second pair) = 40 minutes
        assertEquals(40, playTime)
    }

    @Test
    fun `calculatePlayTime ignores multiple consecutive IN actions`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:10:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // Only counts from first IN (14:00) to OUT (14:30) = 30 minutes
        assertEquals(30, playTime)
    }

    @Test
    fun `calculatePlayTime handles actions not in chronological order`() {
        val actions = listOf(
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // Should sort actions and calculate correctly = 30 minutes
        assertEquals(30, playTime)
    }

    @Test
    fun `calculatePlayTime handles partial pair at end`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:20:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:40:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // Only counts first pair (20 minutes), ignores unpaired final IN
        assertEquals(20, playTime)
    }

    @Test
    fun `calculatePlayTime handles mixed IN OUT and scoring actions`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:15:00",
                actionCount = 1,
                actionType = "GOAL",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // Ignores GOAL action, only counts IN to OUT = 30 minutes
        assertEquals(30, playTime)
    }

    @Test
    fun `calculatePlayTime calculates very short play time correctly`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:01:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertEquals(1, playTime)
    }

    @Test
    fun `calculatePlayTime calculates very long play time correctly`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T16:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // 2 hours and 30 minutes = 150 minutes
        assertEquals(150, playTime)
    }

    @Test
    fun `calculatePlayTime works for training sessions without matchId`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = false,
                playerId = "player-1",
                matchId = ""
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:45:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = false,
                playerId = "player-1",
                matchId = ""
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        assertEquals(45, playTime)
    }

    @Test
    fun `calculatePlayTime ignores consecutive OUT actions`() {
        val actions = listOf(
            SoccerAction(
                id = 1,
                dateTime = "2025-12-28T14:00:00",
                actionCount = 0,
                actionType = "PLAYER_IN",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 2,
                dateTime = "2025-12-28T14:20:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            ),
            SoccerAction(
                id = 3,
                dateTime = "2025-12-28T14:30:00",
                actionCount = 0,
                actionType = "PLAYER_OUT",
                isMatch = true,
                playerId = "player-1"
            )
        )

        val playTime = SoccerAction.calculatePlayTime(actions)

        // First pair: 20 minutes, second OUT ignored (no matching IN)
        assertEquals(20, playTime)
    }
}
