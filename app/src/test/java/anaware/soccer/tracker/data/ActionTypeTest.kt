package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ActionType enum.
 */
class ActionTypeTest {

    @Test
    fun `all returns six action types`() {
        val allTypes = ActionType.all()

        assertEquals(6, allTypes.size)
        assertTrue(allTypes.contains(ActionType.GOAL))
        assertTrue(allTypes.contains(ActionType.ASSIST))
        assertTrue(allTypes.contains(ActionType.OFFENSIVE_ACTION))
        assertTrue(allTypes.contains(ActionType.DUEL_WIN))
        assertTrue(allTypes.contains(ActionType.PLAYER_IN))
        assertTrue(allTypes.contains(ActionType.PLAYER_OUT))
    }

    @Test
    fun `default returns OFFENSIVE_ACTION`() {
        assertEquals(ActionType.OFFENSIVE_ACTION, ActionType.default())
    }

    @Test
    fun `displayName returns correct names`() {
        assertEquals("Goal", ActionType.GOAL.displayName())
        assertEquals("Assist", ActionType.ASSIST.displayName())
        assertEquals("Offensive Action", ActionType.OFFENSIVE_ACTION.displayName())
        assertEquals("Duel Win", ActionType.DUEL_WIN.displayName())
        assertEquals("Player In", ActionType.PLAYER_IN.displayName())
        assertEquals("Player Out", ActionType.PLAYER_OUT.displayName())
    }

    @Test
    fun `enum valueOf works correctly`() {
        assertEquals(ActionType.GOAL, ActionType.valueOf("GOAL"))
        assertEquals(ActionType.ASSIST, ActionType.valueOf("ASSIST"))
        assertEquals(ActionType.OFFENSIVE_ACTION, ActionType.valueOf("OFFENSIVE_ACTION"))
        assertEquals(ActionType.DUEL_WIN, ActionType.valueOf("DUEL_WIN"))
        assertEquals(ActionType.PLAYER_IN, ActionType.valueOf("PLAYER_IN"))
        assertEquals(ActionType.PLAYER_OUT, ActionType.valueOf("PLAYER_OUT"))
    }

    @Test
    fun `isTimeTracking returns true for PLAYER_IN and PLAYER_OUT`() {
        assertTrue(ActionType.PLAYER_IN.isTimeTracking())
        assertTrue(ActionType.PLAYER_OUT.isTimeTracking())
    }

    @Test
    fun `isTimeTracking returns false for scoring actions`() {
        assertFalse(ActionType.GOAL.isTimeTracking())
        assertFalse(ActionType.ASSIST.isTimeTracking())
        assertFalse(ActionType.OFFENSIVE_ACTION.isTimeTracking())
        assertFalse(ActionType.DUEL_WIN.isTimeTracking())
    }

    @Test
    fun `scoringActions returns four types`() {
        val scoringActions = ActionType.scoringActions()

        assertEquals(4, scoringActions.size)
        assertTrue(scoringActions.contains(ActionType.GOAL))
        assertTrue(scoringActions.contains(ActionType.ASSIST))
        assertTrue(scoringActions.contains(ActionType.OFFENSIVE_ACTION))
        assertTrue(scoringActions.contains(ActionType.DUEL_WIN))
        assertFalse(scoringActions.contains(ActionType.PLAYER_IN))
        assertFalse(scoringActions.contains(ActionType.PLAYER_OUT))
    }

    @Test
    fun `timeTrackingActions returns two types`() {
        val timeTrackingActions = ActionType.timeTrackingActions()

        assertEquals(2, timeTrackingActions.size)
        assertTrue(timeTrackingActions.contains(ActionType.PLAYER_IN))
        assertTrue(timeTrackingActions.contains(ActionType.PLAYER_OUT))
        assertFalse(timeTrackingActions.contains(ActionType.GOAL))
        assertFalse(timeTrackingActions.contains(ActionType.ASSIST))
        assertFalse(timeTrackingActions.contains(ActionType.OFFENSIVE_ACTION))
        assertFalse(timeTrackingActions.contains(ActionType.DUEL_WIN))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf throws exception for invalid type`() {
        ActionType.valueOf("INVALID_TYPE")
    }
}
