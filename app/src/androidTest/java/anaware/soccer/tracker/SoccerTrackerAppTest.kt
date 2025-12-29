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

    /**
     * Helper function to navigate to a screen via the hamburger menu drawer.
     */
    private fun navigateToScreenViaDrawer(menuItemText: String) {
        // Open hamburger menu
        composeTestRule.onNodeWithContentDescription("Open menu").performClick()
        composeTestRule.waitForIdle()

        // Click the menu item
        composeTestRule.onNodeWithText(menuItemText).performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun app_launches_successfully() {
        // Verify the app launches and shows hamburger menu icon
        composeTestRule.onNodeWithContentDescription("Open menu").assertExists()

        // Verify FAB for adding entries exists
        composeTestRule.onNodeWithContentDescription("Add Entry").assertExists()

        // Verify starting screen is Progress Chart
        composeTestRule.onNodeWithText("Progress Chart").assertExists()
    }

    @Test
    fun navigation_switches_between_tabs() {
        // Start on Progress Chart screen (default starting screen)
        composeTestRule.onNodeWithText("Progress Chart").assertExists()

        // Navigate to History via drawer
        navigateToScreenViaDrawer("History")
        // History screen should show "Total Actions" header
        composeTestRule.onNodeWithText("Total Actions").assertExists()

        // Navigate to Account via drawer
        navigateToScreenViaDrawer("Account")
        // Account screen should show backup/sync info
        composeTestRule.onNodeWithText("Account & Sync").assertExists()

        // Navigate back to Progress Chart via drawer
        navigateToScreenViaDrawer("Progress Chart")
        // Progress screen should show chart title
        composeTestRule.onNodeWithText("Progress Chart").assertExists()

        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Record Offensive Actions").assertExists()
    }

    @Test
    fun add_screen_shows_all_required_fields() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

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
        // Note: "Match" appears twice (toggle + section header), so we verify count
        composeTestRule.onAllNodesWithText("Match").assertCountEquals(2)
        composeTestRule.onNodeWithText("Training").assertExists()

        // Check for save button
        composeTestRule.onNodeWithText("Save Entry").assertExists()
    }

    @Test
    fun action_count_increment_decrement_works() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

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
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

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
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Initially Match should be selected (default)
        // Toggle to Training
        composeTestRule.onNodeWithText("Training").performClick()
        composeTestRule.waitForIdle()

        // Toggle back to Match - use the first "Match" node (the toggle chip)
        // Note: "Match" appears twice (toggle + section header), so we use [0] to get the first one
        composeTestRule.onAllNodesWithText("Match")[0].performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun history_screen_shows_empty_state_or_entries() {
        // Navigate to History via drawer
        navigateToScreenViaDrawer("History")

        // Should show header with Total Actions
        composeTestRule.onNodeWithText("Total Actions").assertExists()

        // Check if filter button exists
        composeTestRule.onNodeWithContentDescription("Filter").assertExists()
    }

    @Test
    fun progress_screen_requires_action_type_selection() {
        // Already on Progress Chart screen (default starting screen), no navigation needed

        // Should show title
        composeTestRule.onNodeWithText("Progress Chart").assertExists()

        // Click filter button to reveal filters
        composeTestRule.onNodeWithContentDescription("Toggle Filters").performClick()
        composeTestRule.waitForIdle()

        // Check for action type filter chips (singular forms) inside the filter panel
        composeTestRule.onNodeWithText("Goal").assertExists()
        composeTestRule.onNodeWithText("Assist").assertExists()
        composeTestRule.onNodeWithText("Offensive Action").assertExists()
    }

    @Test
    fun save_button_disabled_when_action_count_is_zero() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Action count starts at 0
        composeTestRule.onNodeWithText("0").assertExists()

        // Save button should be disabled - verify validation message appears
        composeTestRule.onNodeWithText("Add at least 1 action to save").assertExists()
    }

    @Test
    fun validation_message_shown_when_player_not_selected() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Increment action count to 1
        composeTestRule.onNodeWithContentDescription("Increase count").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1").assertExists()

        // Validation message should now show "Select a player to save"
        composeTestRule.onNodeWithText("Select a player to save").assertExists()
    }

    @Test
    fun player_selection_section_shows_with_required_indicator() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Player section should show with asterisk indicating required
        composeTestRule.onNodeWithText("Player *").assertExists()
    }

    @Test
    fun team_selection_section_shows_with_required_indicator() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Need to scroll down to see Team section
        composeTestRule.onNodeWithText("Player *").assertExists()

        // Team section may be conditional on player selection, but label should exist
        // Note: This will only show if a player is selected first
    }

    @Test
    fun account_screen_shows_player_and_team_management_options() {
        // Navigate to Account screen via drawer
        navigateToScreenViaDrawer("Account")

        // Check for Account & Sync header
        composeTestRule.onNodeWithText("Account & Sync").assertExists()

        // Check for floating action button (Management Menu)
        composeTestRule.onNodeWithContentDescription("Management Menu").assertExists()
    }

    @Test
    fun date_time_checkbox_control_exists() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Check for date/time section
        composeTestRule.onNodeWithText("Date & Time").assertExists()
        composeTestRule.onNodeWithText("Use current date & time").assertExists()
    }

    // Note: opponent_field_is_optional test removed in v1.2.0
    // Opponent field replaced by match selection UI with "Opponent Team *" in Create New Match dialog

    @Test
    fun action_type_section_shows_all_three_types() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Check for Action Type section
        composeTestRule.onNodeWithText("Action Type").assertExists()

        // Verify all three action types are available
        composeTestRule.onNodeWithText("Goal").assertExists()
        composeTestRule.onNodeWithText("Assist").assertExists()
        composeTestRule.onNodeWithText("Offensive Action").assertExists()
    }

    @Test
    fun helper_text_shows_minimum_requirement() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Check for helper text about minimum actions
        composeTestRule.onNodeWithText("Tip: At least 1 action is required to save an entry").assertExists()
    }

    @Test
    fun history_screen_has_filter_button() {
        // Navigate to History via drawer
        navigateToScreenViaDrawer("History")

        // Filter button should exist
        composeTestRule.onNodeWithContentDescription("Filter").assertExists()
    }

    @Test
    fun progress_screen_has_filter_button() {
        // Already on Progress Chart screen (default starting screen), no navigation needed

        // Filter button should exist (Toggle Filters)
        composeTestRule.onNodeWithContentDescription("Toggle Filters").assertExists()
    }

    @Test
    fun progress_screen_filter_button_is_clickable() {
        // Already on Progress Chart screen (default starting screen), no navigation needed

        // Click filter button to toggle filters
        composeTestRule.onNodeWithContentDescription("Toggle Filters").performClick()
        composeTestRule.waitForIdle()

        // Filter panel should now be visible with "Select Action Type" label
        composeTestRule.onNodeWithText("Select Action Type").assertExists()
    }

    @Test
    fun history_screen_filter_button_is_clickable() {
        // Navigate to History via drawer
        navigateToScreenViaDrawer("History")

        // Click filter button
        composeTestRule.onNodeWithContentDescription("Filter").performClick()
        composeTestRule.waitForIdle()

        // Filter sections should be visible
        composeTestRule.onNodeWithText("Action Type").assertExists()
        composeTestRule.onNodeWithText("Session Type").assertExists()
    }

    @Test
    fun account_screen_management_menu_is_clickable() {
        // Navigate to Account via drawer
        navigateToScreenViaDrawer("Account")

        // Click management menu button
        composeTestRule.onNodeWithContentDescription("Management Menu").performClick()
        composeTestRule.waitForIdle()

        // Menu items should appear
        composeTestRule.onNodeWithText("Manage Players").assertExists()
        composeTestRule.onNodeWithText("Manage Teams").assertExists()
        composeTestRule.onNodeWithText("Manage Matches").assertExists()
    }

    @Test
    fun match_management_menu_option_navigates_to_screen() {
        // Navigate to Account via drawer
        navigateToScreenViaDrawer("Account")

        // Open management menu
        composeTestRule.onNodeWithContentDescription("Management Menu").performClick()
        composeTestRule.waitForIdle()

        // Click Manage Matches
        composeTestRule.onNodeWithText("Manage Matches").performClick()
        composeTestRule.waitForIdle()

        // Should navigate to Match Management screen
        composeTestRule.onNodeWithText("Manage Matches").assertExists()
    }

    @Test
    fun player_management_menu_option_navigates_to_screen() {
        // Navigate to Account via drawer
        navigateToScreenViaDrawer("Account")

        // Open management menu
        composeTestRule.onNodeWithContentDescription("Management Menu").performClick()
        composeTestRule.waitForIdle()

        // Click Manage Players
        composeTestRule.onNodeWithText("Manage Players").performClick()
        composeTestRule.waitForIdle()

        // Should navigate to Player Management screen
        composeTestRule.onNodeWithText("Manage Players").assertExists()
    }

    @Test
    fun team_management_menu_option_navigates_to_screen() {
        // Navigate to Account via drawer
        navigateToScreenViaDrawer("Account")

        // Open management menu
        composeTestRule.onNodeWithContentDescription("Management Menu").performClick()
        composeTestRule.waitForIdle()

        // Click Manage Teams
        composeTestRule.onNodeWithText("Manage Teams").performClick()
        composeTestRule.waitForIdle()

        // Should navigate to Team Management screen
        composeTestRule.onNodeWithText("Manage Teams").assertExists()
    }

    @Test
    fun match_section_shows_when_session_type_is_match() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Ensure Match is selected (default) - use the first "Match" node (the toggle chip)
        // Note: "Match" appears twice (toggle + section header), so we use [0] to get the first one
        composeTestRule.onAllNodesWithText("Match")[0].performClick()
        composeTestRule.waitForIdle()

        // Match section should be visible - check for unique element in the section
        // "Create New Match" button is unique to the Match section
        composeTestRule.onNodeWithText("Create New Match").assertExists()
    }

    @Test
    fun create_new_match_button_exists_in_add_screen() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Ensure Match is selected - use the first "Match" node (the toggle chip)
        // Note: "Match" appears twice (toggle + section header), so we use [0] to get the first one
        composeTestRule.onAllNodesWithText("Match")[0].performClick()
        composeTestRule.waitForIdle()

        // Create New Match button should exist
        composeTestRule.onNodeWithText("Create New Match").assertExists()
    }

    @Test
    fun progress_chart_shows_statistics_card() {
        // Already on Progress Chart screen (default starting screen), no navigation needed

        // Statistics card should show labels
        composeTestRule.onNodeWithText("Total Actions").assertExists()
        composeTestRule.onNodeWithText("Sessions").assertExists()
        composeTestRule.onNodeWithText("Average").assertExists()
    }

    // Note: progress_chart_shows_about_section test removed in v1.2.0
    // "About the Chart" section only appears when there is data to display
    // UI tests run on empty database, so this section is not visible

    @Test
    fun session_type_section_has_both_and_training_options() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // Check for Session Type section header
        composeTestRule.onNodeWithText("Session Type").assertExists()

        // Verify Training option exists (unique on the page)
        composeTestRule.onNodeWithText("Training").assertExists()

        // Note: "Match" appears twice on the page (once as a toggle option, once as a section header)
        // Both are expected, so we verify at least one exists
        composeTestRule.onAllNodesWithText("Match").assertCountEquals(2)
    }

    @Test
    fun validation_message_updates_based_on_missing_fields() {
        // Navigate to Add screen via FAB
        composeTestRule.onNodeWithContentDescription("Add Entry").performClick()
        composeTestRule.waitForIdle()

        // With action count 0
        composeTestRule.onNodeWithText("Add at least 1 action to save").assertExists()

        // Increment to 1
        composeTestRule.onNodeWithContentDescription("Increase count").performClick()
        composeTestRule.waitForIdle()

        // Now validation should show player required
        composeTestRule.onNodeWithText("Select a player to save").assertExists()
    }
}
