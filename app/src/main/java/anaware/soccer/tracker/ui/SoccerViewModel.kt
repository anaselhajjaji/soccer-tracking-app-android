package anaware.soccer.tracker.ui

import anaware.soccer.tracker.backup.FirebaseService
import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.Match
import anaware.soccer.tracker.data.Player
import anaware.soccer.tracker.data.SoccerAction
import anaware.soccer.tracker.data.Team
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // All players from Firebase
    private val _allPlayers = MutableStateFlow<List<Player>>(emptyList())
    val allPlayers: StateFlow<List<Player>> = _allPlayers.asStateFlow()

    // All teams from Firebase
    private val _allTeams = MutableStateFlow<List<Team>>(emptyList())
    val allTeams: StateFlow<List<Team>> = _allTeams.asStateFlow()

    // All matches from Firebase
    private val _allMatches = MutableStateFlow<List<Match>>(emptyList())
    val allMatches: StateFlow<List<Match>> = _allMatches.asStateFlow()

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

    // Distinct players sorted by name
    val distinctPlayers: StateFlow<List<Player>> = _allPlayers.map { players ->
        players.sortedBy { it.name }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Distinct teams sorted by name
    val distinctTeams: StateFlow<List<Team>> = _allTeams.map { teams ->
        teams.sortedBy { it.name }
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
    private var isLoadingData = false // Prevent concurrent loads

    /**
     * Adds a new soccer action record directly to Firebase.
     */
    fun addAction(
        actionCount: Int,
        actionType: ActionType,
        isMatch: Boolean,
        opponent: String,
        playerId: String = "",
        teamId: String = "",
        context: Context? = null
    ) {
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
                opponent = opponent,
                playerId = playerId,
                teamId = teamId
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
    fun addAction(
        actionCount: Int,
        actionType: ActionType,
        isMatch: Boolean,
        dateTime: java.time.LocalDateTime,
        opponent: String,
        playerId: String = "",
        teamId: String = "",
        context: Context? = null
    ) {
        viewModelScope.launch {
            val service = getFirebaseService(context ?: return@launch)

            // 1. If match action, find/create match
            var matchId = ""
            if (isMatch) {
                // Extract date from dateTime
                val matchDate = dateTime.toLocalDate().toString()

                // Find or create opponent team
                val opponentTeamResult = if (opponent.isNotBlank()) {
                    service.findOrCreateOpponentTeam(opponent)
                } else {
                    Result.failure(Exception("Opponent required for match actions"))
                }

                if (opponentTeamResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        message = "Error creating opponent team: ${opponentTeamResult.exceptionOrNull()?.message}"
                    )
                    return@launch
                }

                val opponentTeamId = opponentTeamResult.getOrNull()!!

                // Find or create match
                val matchResult = service.findOrCreateMatch(
                    date = matchDate,
                    playerTeamId = teamId,
                    opponentTeamId = opponentTeamId
                )

                if (matchResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        message = "Error creating match: ${matchResult.exceptionOrNull()?.message}"
                    )
                    return@launch
                }

                matchId = matchResult.getOrNull()!!

                // Refresh matches list
                val matchesResult = service.getAllMatches()
                if (matchesResult.isSuccess) {
                    _allMatches.value = matchesResult.getOrNull()!!
                }

                // Refresh teams list (opponent team may have been created)
                val teamsResult = service.getAllTeams()
                if (teamsResult.isSuccess) {
                    _allTeams.value = teamsResult.getOrNull() ?: emptyList()
                }
            }

            // 2. Create action with matchId
            val actionId = FirebaseService.generateActionId()
            val action = SoccerAction(
                id = actionId,
                dateTime = dateTime.toString(),
                actionCount = actionCount,
                actionType = actionType.name,
                isMatch = isMatch,
                opponent = opponent,
                playerId = playerId,
                teamId = teamId,
                matchId = matchId
            )

            // 3. Save action
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
     * Updates an existing soccer action record in Firebase.
     */
    fun updateAction(action: SoccerAction, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.updateAction(action)

            if (result.isSuccess) {
                // Update in local list immediately for instant UI update
                _allActions.value = _allActions.value.map {
                    if (it.id == action.id) action else it
                }.sortedByDescending { it.dateTime }
                _uiState.value = _uiState.value.copy(
                    message = "Entry updated successfully"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to update entry: ${result.exceptionOrNull()?.message}"
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
        _allPlayers.value = emptyList()
        _allTeams.value = emptyList()
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
    fun getActionsByTypeSessionAndOpponent(
        actionType: ActionType,
        isMatch: Boolean,
        opponent: String
    ): StateFlow<List<SoccerAction>> {
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
    fun getTotalCountByTypeSessionAndOpponent(
        actionType: ActionType,
        isMatch: Boolean,
        opponent: String
    ): StateFlow<Int?> {
        return _allActions.map { actions ->
            actions.filter {
                it.getActionTypeEnum() == actionType && it.isMatch == isMatch && it.opponent == opponent
            }.sumOf { it.actionCount }
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

                    // Load all players from Firebase
                    val playersResult = service.getAllPlayers()
                    if (playersResult.isSuccess) {
                        val players = playersResult.getOrNull() ?: emptyList()
                        _allPlayers.value = players.sortedBy { it.name }
                    }

                    // Load all teams from Firebase
                    val teamsResult = service.getAllTeams()
                    if (teamsResult.isSuccess) {
                        val teams = teamsResult.getOrNull() ?: emptyList()
                        _allTeams.value = teams.sortedBy { it.name }
                    }

                    // Load all matches from Firebase
                    val matchesResult = service.getAllMatches()
                    if (matchesResult.isSuccess) {
                        val matches = matchesResult.getOrNull() ?: emptyList()
                        _allMatches.value = matches.sortedByDescending { it.date }
                    }

                    // Automatically migrate legacy actions
                    if (actionsResult.isSuccess && playersResult.isSuccess) {
                        performAutomaticMigration(service, context)
                    }

                    // Automatically migrate legacy match actions to matches
                    if (actionsResult.isSuccess) {
                        migrateLegacyActionsToMatches(service)
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
     * Automatically migrates legacy actions to a default player.
     * Creates a default player if needed, and assigns all legacy actions to it.
     */
    private suspend fun performAutomaticMigration(service: FirebaseService, context: Context) {
        val legacyActions = _allActions.value.filter { it.isLegacyAction() }
        if (legacyActions.isEmpty()) {
            return // No migration needed
        }

        // Check if a default player already exists
        var defaultPlayer = _allPlayers.value.firstOrNull { it.name == "Player" }

        if (defaultPlayer == null) {
            // Create default player
            val playerId = FirebaseService.generatePlayerId()
            defaultPlayer = Player(
                id = playerId,
                name = "Player",
                birthdate = "2010-01-01",
                number = 0,
                teams = emptyList()
            )

            val addResult = service.addPlayer(defaultPlayer)
            if (addResult.isSuccess) {
                _allPlayers.value = (_allPlayers.value + defaultPlayer).sortedBy { it.name }
            } else {
                return // Failed to create default player
            }
        }

        // Migrate all legacy actions to the default player
        legacyActions.forEach { action ->
            val updateResult = service.updateActionPlayerTeam(
                actionId = action.id,
                playerId = defaultPlayer.id,
                teamId = "" // No team assignment
            )

            if (updateResult.isSuccess) {
                // Update local state
                val updatedAction = action.copy(
                    playerId = defaultPlayer.id,
                    teamId = ""
                )
                val updatedActions = _allActions.value.map {
                    if (it.id == action.id) updatedAction else it
                }
                _allActions.value = updatedActions
            }
        }

        // Show migration status
        _syncStatus.value = "Migrated ${legacyActions.size} legacy entries to '${defaultPlayer.name}'"
    }

    /**
     * Clear sync status message.
     */
    fun clearSyncStatus() {
        _syncStatus.value = null
    }

    // ========== Player Management ==========

    /**
     * Adds a new player to Firebase and local state.
     */
    fun addPlayer(
        name: String,
        birthdate: String,
        number: Int,
        teams: List<String>,
        context: Context
    ) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val playerId = FirebaseService.generatePlayerId()
            val player = Player(
                id = playerId,
                name = name,
                birthdate = birthdate,
                number = number,
                teams = teams
            )

            val result = service.addPlayer(player)
            if (result.isSuccess) {
                _allPlayers.value = (_allPlayers.value + player).sortedBy { it.name }
                _uiState.value = _uiState.value.copy(message = "Player added successfully")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to add player: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Updates an existing player in Firebase and local state.
     */
    fun updatePlayer(player: Player, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.updatePlayer(player)

            if (result.isSuccess) {
                _allPlayers.value = _allPlayers.value.map {
                    if (it.id == player.id) player else it
                }.sortedBy { it.name }
                _uiState.value = _uiState.value.copy(message = "Player updated successfully")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to update player: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Deletes a player from Firebase and local state.
     */
    fun deletePlayer(player: Player, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.deletePlayer(player.id)

            if (result.isSuccess) {
                _allPlayers.value = _allPlayers.value.filter { it.id != player.id }
                _uiState.value = _uiState.value.copy(message = "Player deleted")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete player: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Gets a player by ID.
     */
    fun getPlayerById(playerId: String): Player? {
        return _allPlayers.value.find { it.id == playerId }
    }

    // ========== Team Management ==========

    /**
     * Adds a new team to Firebase and local state.
     */
    fun addTeam(
        name: String,
        color: String,
        league: String,
        season: String,
        context: Context
    ) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val teamId = FirebaseService.generateTeamId()
            val team = Team(
                id = teamId,
                name = name,
                color = color,
                league = league,
                season = season
            )

            val result = service.addTeam(team)
            if (result.isSuccess) {
                _allTeams.value = (_allTeams.value + team).sortedBy { it.name }
                _uiState.value = _uiState.value.copy(message = "Team added successfully")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to add team: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Updates an existing team in Firebase and local state.
     */
    fun updateTeam(team: Team, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.updateTeam(team)

            if (result.isSuccess) {
                _allTeams.value = _allTeams.value.map {
                    if (it.id == team.id) team else it
                }.sortedBy { it.name }
                _uiState.value = _uiState.value.copy(message = "Team updated successfully")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to update team: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Deletes a team from Firebase and local state.
     */
    fun deleteTeam(team: Team, context: Context) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.deleteTeam(team.id)

            if (result.isSuccess) {
                _allTeams.value = _allTeams.value.filter { it.id != team.id }
                _uiState.value = _uiState.value.copy(message = "Team deleted")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete team: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Gets a team by ID.
     */
    fun getTeamById(teamId: String): Team? {
        return _allTeams.value.find { it.id == teamId }
    }

    // ========== Migration Support ==========

    /**
     * Gets all legacy actions (actions without player assignment).
     */
    fun getLegacyActions(): StateFlow<List<SoccerAction>> {
        return _allActions.map { actions ->
            actions.filter { it.isLegacyAction() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Assigns a player and team to an existing action (for migration).
     */
    fun assignPlayerTeamToAction(
        action: SoccerAction,
        playerId: String,
        teamId: String,
        context: Context
    ) {
        viewModelScope.launch {
            val service = getFirebaseService(context)
            val result = service.updateActionPlayerTeam(action.id, playerId, teamId)

            if (result.isSuccess) {
                // Update local state
                _allActions.value = _allActions.value.map {
                    if (it.id == action.id) {
                        it.copy(playerId = playerId, teamId = teamId)
                    } else {
                        it
                    }
                }
                _uiState.value = _uiState.value.copy(message = "Action updated successfully")
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to update action: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    /**
     * Automatically migrates legacy match actions to matches.
     * Creates matches from existing opponent strings and links actions to them.
     * This is idempotent and can be run multiple times safely.
     */
    private suspend fun migrateLegacyActionsToMatches(service: FirebaseService) {
        val legacyMatchActions = _allActions.value.filter {
            it.isMatch && it.matchId.isBlank()
        }

        if (legacyMatchActions.isEmpty()) {
            return // No migration needed
        }

        legacyMatchActions.forEach { action ->
            try {
                // Create opponent team from opponent string
                val opponentTeamId = if (action.opponent.isNotBlank()) {
                    service.findOrCreateOpponentTeam(action.opponent).getOrNull() ?: return@forEach
                } else {
                    return@forEach
                }

                // Create/find match
                val matchDate = action.getLocalDateTime().toLocalDate().toString()
                val matchId = service.findOrCreateMatch(
                    date = matchDate,
                    playerTeamId = action.teamId,
                    opponentTeamId = opponentTeamId
                ).getOrNull() ?: return@forEach

                // Update action with matchId
                val updatedAction = action.copy(matchId = matchId)
                service.addAction(updatedAction) // Overwrites existing

                // Update local state
                _allActions.value = _allActions.value.map {
                    if (it.id == action.id) updatedAction else it
                }
            } catch (e: Exception) {
                // Log error but continue migration
            }
        }

        // Reload matches after migration
        val matchesResult = service.getAllMatches()
        if (matchesResult.isSuccess) {
            _allMatches.value = matchesResult.getOrNull() ?: emptyList()
        }

        // Reload teams (opponent teams may have been created)
        val teamsResult = service.getAllTeams()
        if (teamsResult.isSuccess) {
            _allTeams.value = teamsResult.getOrNull() ?: emptyList()
        }
    }
}

/**
 * UI state for managing dialogs and messages.
 */
data class UiState(
    val message: String? = null
)
