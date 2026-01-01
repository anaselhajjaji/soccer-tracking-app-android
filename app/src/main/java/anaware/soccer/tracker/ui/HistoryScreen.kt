package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Screen displaying the history of all soccer action entries.
 * Allows users to view, delete individual entries, or clear all data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: SoccerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allActions by viewModel.allActions.collectAsState()
    val opponents by viewModel.distinctOpponents.collectAsState()
    val players by viewModel.distinctPlayers.collectAsState()
    val teams by viewModel.distinctTeams.collectAsState()
    val matches by viewModel.allMatches.collectAsState()
    val totalCount by viewModel.totalActionCount.collectAsState()
    var actionToDelete by remember { mutableStateOf<SoccerAction?>(null) }
    var actionToEdit by remember { mutableStateOf<SoccerAction?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Filter states
    var selectedActionType by remember { mutableStateOf<ActionType?>(null) }
    var selectedSessionType by remember {
        mutableStateOf<Boolean?>(
            null
        )
    } // null = Both, true = Match, false = Training
    var selectedOpponentTeamId by remember { mutableStateOf<String?>(null) }
    var selectedPlayerId by remember { mutableStateOf<String?>(null) }
    var selectedTeamId by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var opponentTeamDropdownExpanded by remember { mutableStateOf(false) }
    var teamDropdownExpanded by remember { mutableStateOf(false) }

    // Get opponent teams from matches
    val opponentTeams = remember(matches, teams) {
        matches.mapNotNull { match ->
            teams.find { it.id == match.opponentTeamId }
        }.distinctBy { it.id }
    }

    // Apply filters
    val filteredActions =
        remember(
            allActions,
            selectedActionType,
            selectedSessionType,
            selectedOpponentTeamId,
            selectedPlayerId,
            selectedTeamId
        ) {
            allActions.filter { action ->
                val matchesActionType = selectedActionType == null || action.getActionTypeEnum() == selectedActionType
                val matchesSessionType = selectedSessionType == null || action.isMatch == selectedSessionType

                // Match by opponent team via match
                val matchesOpponentTeam = if (selectedOpponentTeamId == null) {
                    true
                } else {
                    val actionMatch = matches.find { it.id == action.matchId }
                    actionMatch?.opponentTeamId == selectedOpponentTeamId
                }

                val matchesPlayer = selectedPlayerId == null || action.playerId == selectedPlayerId
                val matchesTeam = selectedTeamId == null || action.teamId == selectedTeamId
                matchesActionType && matchesSessionType && matchesOpponentTeam && matchesPlayer && matchesTeam
            }
        }

    // Check if any filters are active
    val hasActiveFilters = selectedActionType != null || selectedSessionType != null || selectedOpponentTeamId != null || selectedPlayerId != null || selectedTeamId != null

    // Pull-to-refresh function
    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.attemptAutoSignIn(context)
        // Delay to show refresh animation
        scope.launch {
            delay(500)
            isRefreshing = false
        }
    }

    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with statistics and filter button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Actions",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${totalCount ?: 0}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        FilledTonalIconButton(
                            onClick = { showFilters = !showFilters },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (hasActiveFilters) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (hasActiveFilters) {
                                    MaterialTheme.colorScheme.onTertiary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        Text(
                            text = "${filteredActions.size} entries",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Filter section
            if (showFilters) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filters",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (hasActiveFilters) {
                                TextButton(
                                    onClick = {
                                        selectedActionType = null
                                        selectedSessionType = null
                                        selectedOpponentTeamId = null
                                        selectedPlayerId = null
                                        selectedTeamId = null
                                    }
                                ) {
                                    Text("Clear All")
                                }
                            }
                        }

                        // Action Type Filter
                        Text(
                            text = "Action Type",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = selectedActionType == null,
                                onClick = { selectedActionType = null },
                                label = { Text("All") }
                            )
                            ActionType.all().forEach { type ->
                                FilterChip(
                                    selected = selectedActionType == type,
                                    onClick = {
                                        selectedActionType = if (selectedActionType == type) null else type
                                    },
                                    label = { Text(type.displayName()) }
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Session Type Filter
                        Text(
                            text = "Session Type",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = selectedSessionType == null,
                                onClick = { selectedSessionType = null },
                                label = { Text("Both") }
                            )
                            FilterChip(
                                selected = selectedSessionType == true,
                                onClick = {
                                    selectedSessionType = if (selectedSessionType == true) null else true
                                },
                                label = { Text("Match") }
                            )
                            FilterChip(
                                selected = selectedSessionType == false,
                                onClick = {
                                    selectedSessionType = if (selectedSessionType == false) null else false
                                },
                                label = { Text("Training") }
                            )
                        }

                        // Opponent Filter (only show if there are opponent teams)
                        if (opponentTeams.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Filter by Opponent",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // "All" opponents chip
                                FilterChip(
                                    selected = selectedOpponentTeamId == null,
                                    onClick = { selectedOpponentTeamId = null },
                                    label = { Text("All") }
                                )

                                // Opponent team dropdown
                                ExposedDropdownMenuBox(
                                    expanded = opponentTeamDropdownExpanded,
                                    onExpandedChange = { opponentTeamDropdownExpanded = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = if (selectedOpponentTeamId != null) {
                                            opponentTeams.find { it.id == selectedOpponentTeamId }?.name ?: "Select Team"
                                        } else {
                                            "Select Team"
                                        },
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Opponent") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = opponentTeamDropdownExpanded
                                        ) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = opponentTeamDropdownExpanded,
                                        onDismissRequest = { opponentTeamDropdownExpanded = false }
                                    ) {
                                        opponentTeams.forEach { team ->
                                            DropdownMenuItem(
                                                text = { Text(team.name) },
                                                onClick = {
                                                    selectedOpponentTeamId = team.id
                                                    opponentTeamDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Player Filter (only show if there are players)
                        if (players.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Filter by Player",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterChip(
                                    selected = selectedPlayerId == null,
                                    onClick = { selectedPlayerId = null },
                                    label = { Text("All") }
                                )

                                players.take(2).forEach { player ->
                                    FilterChip(
                                        selected = selectedPlayerId == player.id,
                                        onClick = {
                                            selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                                        },
                                        label = { Text(player.getDisplayName()) }
                                    )
                                }
                            }

                            // Show remaining players if more than 2
                            if (players.size > 2) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    players.drop(2).chunked(2).forEach { chunk ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            chunk.forEach { player ->
                                                FilterChip(
                                                    selected = selectedPlayerId == player.id,
                                                    onClick = {
                                                        selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                                                    },
                                                    label = { Text(player.getDisplayName()) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Team Filter (only show if there are teams)
                        if (teams.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "Filter by Team",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = selectedTeamId == null,
                                    onClick = { selectedTeamId = null },
                                    label = { Text("All") }
                                )

                                // Team dropdown
                                ExposedDropdownMenuBox(
                                    expanded = teamDropdownExpanded,
                                    onExpandedChange = { teamDropdownExpanded = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = if (selectedTeamId != null) {
                                            teams.find { it.id == selectedTeamId }?.getDisplayName() ?: "Select Team"
                                        } else {
                                            "Select Team"
                                        },
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Team") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = teamDropdownExpanded
                                        ) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = teamDropdownExpanded,
                                        onDismissRequest = { teamDropdownExpanded = false }
                                    ) {
                                        teams.forEach { team ->
                                            DropdownMenuItem(
                                                text = { Text(team.getDisplayName()) },
                                                onClick = {
                                                    selectedTeamId = team.id
                                                    teamDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // List of actions
            if (filteredActions.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsScore,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (hasActiveFilters) "No matching entries" else "No entries yet",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (hasActiveFilters) "Try adjusting your filters" else "Start tracking offensive actions!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredActions, key = { it.id }) { action ->
                        ActionEntryCard(
                            action = action,
                            viewModel = viewModel,
                            teams = teams,
                            matches = matches,
                            onEditClick = { actionToEdit = action },
                            onDeleteClick = { actionToDelete = action }
                        )
                    }
                }
            }
        }
    }

    // Edit action dialog
    if (actionToEdit != null) {
        EditActionDialog(
            action = actionToEdit!!,
            viewModel = viewModel,
            players = players,
            teams = teams,
            onDismiss = { actionToEdit = null },
            onSave = { updatedAction ->
                viewModel.updateAction(updatedAction, context)
                actionToEdit = null
            }
        )
    }

    // Delete confirmation dialog
    if (actionToDelete != null) {
        AlertDialog(
            onDismissRequest = { actionToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            title = { Text("Delete Entry?") },
            text = {
                Text("Are you sure you want to delete this entry? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        actionToDelete?.let { viewModel.deleteAction(it, context) }
                        actionToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { actionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying a single soccer action entry.
 */
@Composable
private fun ActionEntryCard(
    action: SoccerAction,
    viewModel: SoccerViewModel,
    teams: List<anaware.soccer.tracker.data.Team>,
    matches: List<anaware.soccer.tracker.data.Match>,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date and time
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = action.getFormattedDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = action.getFormattedTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action count and type (or just action type for time-tracking)
                Text(
                    text = if (action.getActionTypeEnum().isTimeTracking()) {
                        // For time-tracking, just show the action type
                        action.getActionTypeEnum().displayName()
                    } else {
                        // For scoring actions, show count + type
                        "${action.actionCount} ${action.getActionTypeEnum().displayName()}${if (action.actionCount != 1) "s" else ""}"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                // Show time for time-tracking actions
                if (action.getActionTypeEnum().isTimeTracking()) {
                    Text(
                        text = "at ${action.getFormattedTime()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Session type and action type badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (action.isMatch) "Match" else "Training",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = action.getActionTypeEnum().displayName(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }

                // Match name if present (replaces opponent and team display)
                if (action.matchId.isNotBlank()) {
                    val match = matches.find { it.id == action.matchId }
                    if (match != null) {
                        val playerTeam = teams.find { it.id == match.playerTeamId }?.name ?: "Unknown"
                        val opponentTeam = teams.find { it.id == match.opponentTeamId }?.name ?: "Unknown"
                        Text(
                            text = "$playerTeam vs $opponentTeam",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else if (action.isLegacyAction()) {
                    // Show "Legacy Entry" badge for actions without player
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "Legacy Entry",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    // Show player information only (no team or opponent)
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        val player = viewModel.getPlayerById(action.playerId)

                        if (player != null) {
                            Text(
                                text = "Player: ${player.getDisplayName()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Edit and Delete buttons
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit entry",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Dialog for editing an existing soccer action entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditActionDialog(
    action: SoccerAction,
    viewModel: SoccerViewModel,
    players: List<anaware.soccer.tracker.data.Player>,
    teams: List<anaware.soccer.tracker.data.Team>,
    onDismiss: () -> Unit,
    onSave: (SoccerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val actionDateTime = LocalDateTime.parse(action.dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    var actionCount by remember { mutableIntStateOf(action.actionCount) }
    var actionType by remember { mutableStateOf(action.getActionTypeEnum()) }
    var isMatch by remember { mutableStateOf(action.isMatch) }
    var opponent by remember { mutableStateOf(action.opponent) }
    var selectedPlayerId by remember { mutableStateOf(action.playerId) }
    var selectedTeamId by remember { mutableStateOf(action.teamId) }
    var selectedDate by remember { mutableStateOf(actionDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(actionDateTime.toLocalTime()) }

    var showPlayerDropdown by remember { mutableStateOf(false) }
    var showTeamDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Edit Entry",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Action Count
                Text(
                    text = "Action Count: $actionCount",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { if (actionCount > 0) actionCount-- },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Decrease")
                    }
                    FilledTonalIconButton(
                        onClick = { actionCount++ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, "Increase")
                    }
                }

                // Action Type
                Text(
                    text = "Action Type",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    ActionType.all().forEach { type ->
                        FilterChip(
                            selected = actionType == type,
                            onClick = { actionType = type },
                            label = { Text(type.displayName(), style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Session Type
                Text(
                    text = "Session Type",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    FilterChip(
                        selected = isMatch,
                        onClick = { isMatch = true },
                        label = { Text("Match") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isMatch,
                        onClick = { isMatch = false },
                        label = { Text("Training") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Opponent
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                // Player Selection
                if (players.isNotEmpty()) {
                    Text(
                        text = "Player",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        OutlinedButton(
                            onClick = { showPlayerDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (selectedPlayerId.isNotBlank()) {
                                    viewModel.getPlayerById(selectedPlayerId)?.getDisplayName() ?: "Select Player"
                                } else {
                                    "Select Player"
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = showPlayerDropdown,
                            onDismissRequest = { showPlayerDropdown = false }
                        ) {
                            players.forEach { player ->
                                DropdownMenuItem(
                                    text = { Text(player.getDisplayName()) },
                                    onClick = {
                                        selectedPlayerId = player.id
                                        selectedTeamId = "" // Reset team
                                        showPlayerDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Team Selection
                if (selectedPlayerId.isNotBlank() && teams.isNotEmpty()) {
                    val selectedPlayer = viewModel.getPlayerById(selectedPlayerId)
                    val playerTeams = selectedPlayer?.teams?.mapNotNull { teamId ->
                        viewModel.getTeamById(teamId)
                    } ?: emptyList()

                    if (playerTeams.isNotEmpty()) {
                        Text(
                            text = "Team",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            OutlinedButton(
                                onClick = { showTeamDropdown = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedTeamId.isNotBlank()) {
                                        viewModel.getTeamById(selectedTeamId)?.getDisplayName() ?: "Select Team"
                                    } else {
                                        "Select Team"
                                    }
                                )
                            }
                            DropdownMenu(
                                expanded = showTeamDropdown,
                                onDismissRequest = { showTeamDropdown = false }
                            ) {
                                playerTeams.forEach { team ->
                                    DropdownMenuItem(
                                        text = { Text(team.getDisplayName()) },
                                        onClick = {
                                            selectedTeamId = team.id
                                            showTeamDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                            val updatedAction = action.copy(
                                actionCount = actionCount,
                                actionType = actionType.name,
                                isMatch = isMatch,
                                opponent = opponent,
                                playerId = selectedPlayerId,
                                teamId = selectedTeamId,
                                dateTime = dateTime.toString()
                            )
                            onSave(updatedAction)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
