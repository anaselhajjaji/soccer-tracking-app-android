package anaware.soccer.tracker.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ActionType enum.
 */
class ActionTypeTest {

    @Test
    fun `all returns three action types`() {
        val allTypes = ActionType.all()

        assertEquals(3, allTypes.size)
        assertTrue(allTypes.contains(ActionType.GOAL))
        assertTrue(allTypes.contains(ActionType.ASSIST))
        assertTrue(allTypes.contains(ActionType.OFFENSIVE_ACTION))
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
    }

    @Test
    fun `enum valueOf works correctly`() {
        assertEquals(ActionType.GOAL, ActionType.valueOf("GOAL"))
        assertEquals(ActionType.ASSIST, ActionType.valueOf("ASSIST"))
        assertEquals(ActionType.OFFENSIVE_ACTION, ActionType.valueOf("OFFENSIVE_ACTION"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf throws exception for invalid type`() {
        ActionType.valueOf("INVALID_TYPE")
    }
}
