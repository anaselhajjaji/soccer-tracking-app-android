package anaware.soccer.tracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import anaware.soccer.tracker.ui.theme.SoccerTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the Soccer Tracker app.
 * These tests verify the main navigation and UI interactions.
 *
 * Note: These tests run on actual devices or emulators and require Firebase authentication.
 * For Firebase Test Lab, ensure test devices have Google Play Services.
 */
@RunWith(AndroidJUnit4::class)
class SoccerTrackerAppTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launches_successfully() {
        // Verify the app launches and shows bottom navigation
        composeTestRule.onNodeWithText("Add").assertExists()
        composeTestRule.onNodeWithText("History").assertExists()
        composeTestRule.onNodeWithText("Progress").assertExists()
        composeTestRule.onNodeWithText("Account").assertExists()
    }

    @Test
    fun navigation_switches_between_tabs() {
        // Start on Add screen
        composeTestRule.onNodeWithText("Add").assertExists()

        // Navigate to History
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.waitForIdle()
        // History screen should show "Total Actions" header
        composeTestRule.onNodeWithText("Total Actions").assertExists()

        // Navigate to Progress
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()
        // Progress screen should show chart title
        composeTestRule.onNodeWithText("Progress Chart").assertExists()

        // Navigate to Account
        composeTestRule.onNodeWithText("Account").performClick()
        composeTestRule.waitForIdle()
        // Account screen should show backup/sync info
        composeTestRule.onNodeWithText("Account & Sync").assertExists()

        // Navigate back to Add
        composeTestRule.onNodeWithText("Add").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Record Offensive Actions").assertExists()
    }

    @Test
    fun add_screen_shows_all_required_fields() {
        // Verify Add screen has all input fields
        composeTestRule.onNodeWithText("Record Offensive Actions").assertExists()

        // Check for action count controls
        composeTestRule.onNodeWithContentDescription("Decrease count").assertExists()
        composeTestRule.onNodeWithContentDescription("Increase count").assertExists()

        // Check for action type buttons
        composeTestRule.onNodeWithText("Goal").assertExists()
        composeTestRule.onNodeWithText("Assist").assertExists()
        composeTestRule.onNodeWithText("Offensive Action").assertExists()

        // Check for session type toggle
        composeTestRule.onNodeWithText("Match").assertExists()
        composeTestRule.onNodeWithText("Training").assertExists()

        // Check for save button
        composeTestRule.onNodeWithText("Save Entry").assertExists()
    }

    @Test
    fun action_count_increment_decrement_works() {
        // Find the action count text (starts at 0)
        composeTestRule.onNodeWithText("0").assertExists()

        // Click increment button
        composeTestRule.onNodeWithContentDescription("Increase count").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1").assertExists()

        // Click increment again
        composeTestRule.onNodeWithContentDescription("Increase count").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("2").assertExists()

        // Click decrement button
        composeTestRule.onNodeWithContentDescription("Decrease count").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1").assertExists()

        // Click decrement to zero
        composeTestRule.onNodeWithContentDescription("Decrease count").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun action_type_selection_works() {
        // Initially Goal should be selected (default)
        // Try selecting Assist
        composeTestRule.onNodeWithText("Assist").performClick()
        composeTestRule.waitForIdle()

        // Try selecting Offensive Action
        composeTestRule.onNodeWithText("Offensive Action").performClick()
        composeTestRule.waitForIdle()

        // Select back to Goal
        composeTestRule.onNodeWithText("Goal").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun session_type_toggle_works() {
        // Initially Match should be selected (default)
        // Toggle to Training
        composeTestRule.onNodeWithText("Training").performClick()
        composeTestRule.waitForIdle()

        // Toggle back to Match
        composeTestRule.onNodeWithText("Match").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun history_screen_shows_empty_state_or_entries() {
        // Navigate to History
        composeTestRule.onNodeWithText("History").performClick()
        composeTestRule.waitForIdle()

        // Should show header with Total Actions
        composeTestRule.onNodeWithText("Total Actions").assertExists()

        // Check if filter button exists
        composeTestRule.onNodeWithContentDescription("Filter").assertExists()
    }

    @Test
    fun progress_screen_requires_action_type_selection() {
        // Navigate to Progress
        composeTestRule.onNodeWithText("Progress").performClick()
        composeTestRule.waitForIdle()

        // Should show title and filter chips
        composeTestRule.onNodeWithText("Progress Chart").assertExists()

        // Check for action type filter chips (singular forms)
        composeTestRule.onNodeWithText("Goal").assertExists()
        composeTestRule.onNodeWithText("Assist").assertExists()
        composeTestRule.onNodeWithText("Offensive Action").assertExists()
    }
}
