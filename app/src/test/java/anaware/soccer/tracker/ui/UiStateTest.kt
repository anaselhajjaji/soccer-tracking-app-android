package anaware.soccer.tracker.ui

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for UiState data class.
 */
class UiStateTest {

    @Test
    fun `UiState default constructor creates instance with null message`() {
        val uiState = UiState()

        assertNull(uiState.message)
    }

    @Test
    fun `UiState constructor with message creates instance correctly`() {
        val message = "Test message"
        val uiState = UiState(message = message)

        assertEquals(message, uiState.message)
    }

    @Test
    fun `UiState copy creates new instance with updated message`() {
        val original = UiState(message = "Original")
        val updated = original.copy(message = "Updated")

        assertEquals("Original", original.message)
        assertEquals("Updated", updated.message)
        assertNotSame(original, updated)
    }

    @Test
    fun `UiState copy with null message clears message`() {
        val original = UiState(message = "Has message")
        val cleared = original.copy(message = null)

        assertEquals("Has message", original.message)
        assertNull(cleared.message)
    }

    @Test
    fun `UiState equals compares by value not reference`() {
        val uiState1 = UiState(message = "Test")
        val uiState2 = UiState(message = "Test")

        assertEquals(uiState1, uiState2)
        assertNotSame(uiState1, uiState2)
    }

    @Test
    fun `UiState hashCode is consistent for equal instances`() {
        val uiState1 = UiState(message = "Test")
        val uiState2 = UiState(message = "Test")

        assertEquals(uiState1.hashCode(), uiState2.hashCode())
    }

    @Test
    fun `UiState toString returns readable string representation`() {
        val uiState = UiState(message = "Test message")

        val stringRepresentation = uiState.toString()

        assertTrue(stringRepresentation.contains("UiState"))
        assertTrue(stringRepresentation.contains("Test message"))
    }

    @Test
    fun `UiState with empty string message is different from null`() {
        val withEmpty = UiState(message = "")
        val withNull = UiState(message = null)

        assertNotEquals(withEmpty, withNull)
        assertEquals("", withEmpty.message)
        assertNull(withNull.message)
    }

    @Test
    fun `UiState with long message handles text correctly`() {
        val longMessage = "This is a very long error message that might occur in the application " +
                "when something goes wrong with Firebase operations or network connectivity"

        val uiState = UiState(message = longMessage)

        assertEquals(longMessage, uiState.message)
        assertTrue(uiState.message!!.length > 50)
    }

    @Test
    fun `UiState with special characters in message handles correctly`() {
        val specialMessage = "Error: Failed to connect!\n\tRetry? (y/n)"

        val uiState = UiState(message = specialMessage)

        assertEquals(specialMessage, uiState.message)
        assertTrue(uiState.message!!.contains("\n"))
        assertTrue(uiState.message!!.contains("\t"))
    }
}
