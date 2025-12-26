package anaware.soccer.tracker.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anaware.soccer.tracker.backup.FirebaseService
import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing soccer action data and UI state.
 * Uses Firebase Firestore for cloud-first data storage.
 */
class SoccerViewModel : ViewModel() {

    // All actions from Firebase (manually managed)
    private val _allActions = MutableStateFlow<List<SoccerAction>>(emptyList())
    val allActions: StateFlow<List<SoccerAction>> = _allActions.asStateFlow()

    // Actions for chart display
    val chartActions: StateFlow<List<SoccerAction>> = _allActions

    // Total action count
    val totalActionCount: StateFlow<Int?> = _allActions.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Distinct opponents for autocomplete
    val distinctOpponents: StateFlow<List<String>> = _allActions.map { actions ->
        actions.mapNotNull { it.opponent.takeIf { op -> op.isNotBlank() } }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI state for showing dialogs and messages
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Auto-sync state
    private val _autoSyncEnabled = MutableStateFlow(false)
    val autoSyncEnabled: StateFlow<Boolean> = _autoSyncEnabled.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private var firebaseService: FirebaseService? = null
    private var isLoadingData = false  // Prevent concurrent loads

    /**
     * Adds a new soccer action record directly to Firebase.
     */
    fun addAction(actionCount: Int, actionType: ActionType, isMatch: Boolean, opponent: String, context: Context? = null) {
        viewModelScope.launch {
            val service = getFirebaseService(context ?: return@launch)

            // Generate unique ID
            val actionId = FirebaseService.generateActionId()
            val action = SoccerAction(
                id = actionId,
                dateTime = java.time.LocalDateTime.now().toString(),
                actionCount = actionCount,
                actionType = actionType.name,
                isMatch = isMatch,
                opponent = opponent
            )

            val result = service.addAction(action)
            if (result.isSuccess) {
                // Add to local list immediately for instant UI update
                _allActions.value = (_allActions.value + action).sortedByDescending { it.dateTime }
                _uiState.value = _uiState.value.copy(
                    message = "Action recorded successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to save action: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Adds a new soccer action record with custom date/time directly to Firebase.
     */
    fun addAction(actionCount: Int, actionType: ActionType, isMatch: Boolean, dateTime: java.time.LocalDateTime, opponent: String, context: Context? = null) {
        viewModelScope.launch {
            val service = getFirebaseService(context ?: return@launch)

            // Generate unique ID
            val actionId = FirebaseService.generateActionId()
            val action = SoccerAction(
                id = actionId,
                dateTime = dateTime.toString(),
                actionCount = actionCount,
                actionType = actionType.name,
                isMatch = isMatch,
                opponent = opponent
            )

            val result = service.addAction(action)
            if (result.isSuccess) {
                // Add to local list immediately for instant UI update
                _allActions.value = (_allActions.value + action).sortedByDescending { it.dateTime }
                _uiState.value = _uiState.value.copy(
                    message = "Action recorded successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to save action: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Deletes a specific soccer action record from Firebase.
     */
    fun deleteAction(action: SoccerAction, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.deleteAction(action.id)

            if (result.isSuccess) {
                // Remove from local list immediately for instant UI update
                _allActions.value = _allActions.value.filter { it.id != action.id }
                _uiState.value = _uiState.value.copy(
                    message = "Entry deleted"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete entry: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Clears the current UI message.
     */
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    /**
     * Gets actions filtered by action type.
     */
    fun getActionsByType(actionType: ActionType): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Gets total count filtered by action type.
     */
    fun getTotalCountByType(actionType: ActionType): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType }.sumOf { it.actionCount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    /**
     * Gets actions filtered by session type (match/training).
     */
    fun getActionsBySessionType(isMatch: Boolean): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.isMatch == isMatch }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Gets total count filtered by session type.
     */
    fun getTotalCountBySessionType(isMatch: Boolean): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter { it.isMatch == isMatch }.sumOf { it.actionCount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    /**
     * Gets actions filtered by both action type and session type.
     */
    fun getActionsByTypeAndSessionType(actionType: ActionType, isMatch: Boolean): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.isMatch == isMatch }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Gets total count filtered by both action type and session type.
     */
    fun getTotalCountByTypeAndSessionType(actionType: ActionType, isMatch: Boolean): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.isMatch == isMatch }.sumOf { it.actionCount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    /**
     * Gets the Firebase service instance (cached).
     */
    fun getFirebaseService(context: Context): FirebaseService {
        if (firebaseService == null) {
            firebaseService = FirebaseService(context)
        }
        return firebaseService!!
    }

    /**
     * Signs out from Firebase and clears data.
     */
    suspend fun signOutFromFirebase(context: Context) {
        val service = getFirebaseService(context)
        service.signOut()
        _autoSyncEnabled.value = false
        _allActions.value = emptyList()
        _syncStatus.value = "Signed out"
    }

    /**
     * Gets actions filtered by action type and opponent.
     */
    fun getActionsByTypeAndOpponent(actionType: ActionType, opponent: String): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.opponent == opponent }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Gets total count filtered by action type and opponent.
     */
    fun getTotalCountByTypeAndOpponent(actionType: ActionType, opponent: String): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.opponent == opponent }.sumOf { it.actionCount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    /**
     * Gets actions filtered by action type, session type, and opponent.
     */
    fun getActionsByTypeSessionAndOpponent(actionType: ActionType, isMatch: Boolean, opponent: String): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.isMatch == isMatch && it.opponent == opponent }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Gets total count filtered by action type, session type, and opponent.
     */
    fun getTotalCountByTypeSessionAndOpponent(actionType: ActionType, isMatch: Boolean, opponent: String): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter { it.getActionTypeEnum() == actionType && it.isMatch == isMatch && it.opponent == opponent }.sumOf { it.actionCount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    /**
     * Attempt automatic silent sign-in and load data from Firebase on app startup.
     */
    fun attemptAutoSignIn(context: Context) {
        viewModelScope.launch {
            // Prevent concurrent loading
            if (isLoadingData) {
                return@launch
            }
            isLoadingData = true

            try {
                _syncStatus.value = "Signing in..."
                val service = getFirebaseService(context)
                val result = service.silentSignIn()

                if (result.isSuccess) {
                    _autoSyncEnabled.value = true
                    _syncStatus.value = "Loading data..."

                    // Load all actions from Firebase
                    val actionsResult = service.getAllActions()

                    if (actionsResult.isSuccess) {
                        val actions = actionsResult.getOrNull() ?: emptyList()
                        _allActions.value = actions.sortedByDescending { it.dateTime }

                        _syncStatus.value = if (actions.isNotEmpty()) {
                            "Loaded ${actions.size} entries"
                        } else {
                            "No entries yet"
                        }
                    } else {
                        _syncStatus.value = "Failed to load data: ${actionsResult.exceptionOrNull()?.message}"
                    }
                } else {
                    _autoSyncEnabled.value = false
                    _syncStatus.value = "Not signed in - please sign in to use the app"
                }
            } finally {
                isLoadingData = false
            }
        }
    }

    /**
     * Clear sync status message.
     */
    fun clearSyncStatus() {
        _syncStatus.value = null
    }

}

/**
 * UI state for managing dialogs and messages.
 */
data class UiState(
    val message: String? = null
)

