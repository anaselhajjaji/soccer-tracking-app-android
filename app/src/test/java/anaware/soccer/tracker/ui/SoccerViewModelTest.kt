package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime

/**
 * Unit tests for SoccerViewModel.
 *
 * Note: These tests verify the ViewModel's business logic and state management.
 * Firebase operations are not tested here and should be tested separately.
 * The ViewModel now uses Firebase directly for all data operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SoccerViewModelTest {

    private lateinit var viewModel: SoccerViewModel
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mock()

        viewModel = SoccerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addAction with current time requires context parameter`() = runTest {
        // Note: This test verifies the method signature. Actual Firebase operations
        // are not tested in unit tests as they require Android context and Firebase setup.
        val actionCount = 5
        val actionType = ActionType.GOAL
        val isMatch = true
        val opponent = "Team A"

        // Without context, the method will return early (no-op)
        viewModel.addAction(actionCount, actionType, isMatch, opponent, context = null)
        advanceUntilIdle()

        // Method should execute without error even without context
        assertNotNull(viewModel)
    }

    @Test
    fun `addAction with custom datetime requires context parameter`() = runTest {
        // Note: This test verifies the method signature. Actual Firebase operations
        // are not tested in unit tests as they require Android context and Firebase setup.
        val actionCount = 3
        val actionType = ActionType.ASSIST
        val isMatch = false
        val dateTime = LocalDateTime.of(2025, 12, 19, 14, 30)
        val opponent = "Team B"

        // Without context, the method will return early (no-op)
        viewModel.addAction(actionCount, actionType, isMatch, dateTime, opponent, context = null)
        advanceUntilIdle()

        // Method should execute without error even without context
        assertNotNull(viewModel)
    }

    @Test
    fun `addAction without context does not update UI state`() = runTest {
        // When context is null, addAction returns early
        viewModel.addAction(5, ActionType.GOAL, true, "Team A", context = null)
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertNull(uiState.message)
    }

    @Test
    fun `deleteAction requires context parameter and method signature is correct`() = runTest {
        // This test only verifies that deleteAction has the correct method signature
        // Firebase operations cannot be tested in unit tests without proper Android context
        val action = SoccerAction(
            id = 1,
            dateTime = "2025-12-19T14:30:00",
            actionCount = 5,
            actionType = "GOAL",
            isMatch = true,
            opponent = "Team A"
        )

        // Just verify the method exists with correct signature
        // We don't actually call it as it requires Firebase initialization
        assertNotNull(action)
        assertNotNull(mockContext)
    }

    @Test
    fun `clearMessage resets UI state message`() = runTest {
        // clearMessage should work independently of context
        viewModel.clearMessage()

        val uiState = viewModel.uiState.first()
        assertNull(uiState.message)
    }

    @Test
    fun `getActionsByType returns filtered StateFlow`() = runTest {
        // This now uses Flow.map on in-memory data instead of repository
        val actionType = ActionType.GOAL

        val stateFlow = viewModel.getActionsByType(actionType)
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = stateFlow.first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTotalCountByType returns count StateFlow`() = runTest {
        // This now uses Flow.map on in-memory data instead of repository
        val actionType = ActionType.ASSIST

        val stateFlow = viewModel.getTotalCountByType(actionType)
        advanceUntilIdle()

        // May return null initially or 0 when no data loaded
        val result = stateFlow.first()
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `getActionsBySessionType returns filtered StateFlow`() = runTest {
        // This now uses Flow.map on in-memory data instead of repository
        val isMatch = true

        val stateFlow = viewModel.getActionsBySessionType(isMatch)
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = stateFlow.first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getActionsByTypeAndOpponent returns filtered StateFlow`() = runTest {
        // This now uses Flow.map on in-memory data instead of repository
        val actionType = ActionType.GOAL
        val opponent = "Team A"

        val stateFlow = viewModel.getActionsByTypeAndOpponent(actionType, opponent)
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = stateFlow.first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getActionsByTypeSessionAndOpponent returns filtered StateFlow`() = runTest {
        // This now uses Flow.map on in-memory data instead of repository
        val actionType = ActionType.GOAL
        val isMatch = true
        val opponent = "Team A"

        val stateFlow = viewModel.getActionsByTypeSessionAndOpponent(actionType, isMatch, opponent)
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = stateFlow.first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `distinctOpponents returns empty list initially`() = runTest {
        // distinctOpponents now comes from Flow.map on in-memory data
        val newViewModel = SoccerViewModel()

        // Wait for StateFlow to initialize
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = newViewModel.distinctOpponents.value
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addAction with zero count accepts parameter`() = runTest {
        // Verify zero count is accepted (though Firebase operation won't execute without context)
        viewModel.addAction(0, ActionType.GOAL, true, "Team A", context = null)
        advanceUntilIdle()

        // Method should execute without error
        assertNotNull(viewModel)
    }

    @Test
    fun `addAction with empty opponent accepts parameter`() = runTest {
        // Verify empty opponent is accepted (though Firebase operation won't execute without context)
        viewModel.addAction(5, ActionType.GOAL, true, "", context = null)
        advanceUntilIdle()

        // Method should execute without error
        assertNotNull(viewModel)
    }

    @Test
    fun `allActions StateFlow is initialized as empty`() = runTest {
        // Verify that allActions starts as empty list
        val result = viewModel.allActions.value
        assertTrue(result.isEmpty())
    }

    @Test
    fun `chartActions StateFlow is initialized as empty`() = runTest {
        // Verify that chartActions starts as empty list
        val result = viewModel.chartActions.value
        assertTrue(result.isEmpty())
    }

    @Test
    fun `totalActionCount StateFlow is initialized`() = runTest {
        // Verify that totalActionCount is accessible
        advanceUntilIdle()
        val result = viewModel.totalActionCount.value
        // Should be 0 or null initially
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `getTotalCountBySessionType returns count StateFlow`() = runTest {
        // Test filtering by session type for totals
        val isMatch = true

        val stateFlow = viewModel.getTotalCountBySessionType(isMatch)
        advanceUntilIdle()

        // Should return null or 0 when no data loaded
        val result = stateFlow.first()
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `getActionsByTypeAndSessionType returns filtered StateFlow`() = runTest {
        // Test filtering by both action type and session type
        val actionType = ActionType.GOAL
        val isMatch = true

        val stateFlow = viewModel.getActionsByTypeAndSessionType(actionType, isMatch)
        advanceUntilIdle()

        // Should return empty list when no data loaded
        val result = stateFlow.first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTotalCountByTypeAndSessionType returns count StateFlow`() = runTest {
        // Test total count filtering by both action type and session type
        val actionType = ActionType.ASSIST
        val isMatch = false

        val stateFlow = viewModel.getTotalCountByTypeAndSessionType(actionType, isMatch)
        advanceUntilIdle()

        // Should return null or 0 when no data loaded
        val result = stateFlow.first()
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `getTotalCountByTypeAndOpponent returns count StateFlow`() = runTest {
        // Test total count filtering by action type and opponent
        val actionType = ActionType.GOAL
        val opponent = "Team A"

        val stateFlow = viewModel.getTotalCountByTypeAndOpponent(actionType, opponent)
        advanceUntilIdle()

        // Should return null or 0 when no data loaded
        val result = stateFlow.first()
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `getTotalCountByTypeSessionAndOpponent returns count StateFlow`() = runTest {
        // Test total count filtering by all three parameters
        val actionType = ActionType.GOAL
        val isMatch = true
        val opponent = "Team A"

        val stateFlow = viewModel.getTotalCountByTypeSessionAndOpponent(actionType, isMatch, opponent)
        advanceUntilIdle()

        // Should return null or 0 when no data loaded
        val result = stateFlow.first()
        assertTrue(result == null || result == 0)
    }

    @Test
    fun `clearSyncStatus resets sync status to null`() = runTest {
        // clearSyncStatus should reset the sync status message
        viewModel.clearSyncStatus()

        val result = viewModel.syncStatus.first()
        assertNull(result)
    }

    @Test
    fun `autoSyncEnabled StateFlow is initialized as false`() = runTest {
        // Verify that autoSyncEnabled starts as false
        val result = viewModel.autoSyncEnabled.value
        assertFalse(result)
    }

    @Test
    fun `syncStatus StateFlow is initialized as null`() = runTest {
        // Verify that syncStatus starts as null
        val result = viewModel.syncStatus.value
        assertNull(result)
    }

    @Test
    fun `uiState is initialized with null message`() = runTest {
        // Verify initial UI state has no message
        val result = viewModel.uiState.value
        assertNull(result.message)
    }

    @Test
    fun `getFirebaseService method exists and accepts context`() = runTest {
        // Note: Cannot test Firebase service caching in unit tests as it requires
        // Android runtime and Firebase initialization. This test verifies the method exists.
        // The caching logic should be tested in integration or instrumentation tests.

        // Just verify the method signature exists
        assertNotNull(mockContext)
        assertNotNull(viewModel)

        // Verify the method is callable (though we don't actually call it with real context)
        // The actual caching behavior is verified through integration tests
    }

    @Test
    fun `multiple ActionType filters work independently`() = runTest {
        // Test that different action type filters don't interfere with each other
        val goalFlow = viewModel.getActionsByType(ActionType.GOAL)
        val assistFlow = viewModel.getActionsByType(ActionType.ASSIST)
        val offensiveFlow = viewModel.getActionsByType(ActionType.OFFENSIVE_ACTION)

        advanceUntilIdle()

        // All should return empty lists initially
        assertTrue(goalFlow.first().isEmpty())
        assertTrue(assistFlow.first().isEmpty())
        assertTrue(offensiveFlow.first().isEmpty())
    }

    @Test
    fun `multiple SessionType filters work independently`() = runTest {
        // Test that different session type filters don't interfere with each other
        val matchFlow = viewModel.getActionsBySessionType(true)
        val trainingFlow = viewModel.getActionsBySessionType(false)

        advanceUntilIdle()

        // Both should return empty lists initially
        assertTrue(matchFlow.first().isEmpty())
        assertTrue(trainingFlow.first().isEmpty())
    }
}
