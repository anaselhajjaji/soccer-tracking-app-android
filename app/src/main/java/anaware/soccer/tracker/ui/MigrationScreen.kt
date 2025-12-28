package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.Player
import anaware.soccer.tracker.data.SoccerAction
import anaware.soccer.tracker.data.Team
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Screen for migrating legacy actions (actions without player/team assignments).
 * Allows users to assign players and teams to existing actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigrationScreen(
    viewModel: SoccerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val legacyActions by viewModel.getLegacyActions().collectAsState()
    val players by viewModel.distinctPlayers.collectAsState()
    val teams by viewModel.distinctTeams.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Migrate Legacy Entries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Warning card if no players or teams
            if (players.isEmpty() || teams.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Players or Teams Missing",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = if (players.isEmpty() && teams.isEmpty()) {
                                    "You need to add at least one player and one team before migrating entries."
                                } else if (players.isEmpty()) {
                                    "You need to add at least one player before migrating entries."
                                } else {
                                    "You need to add at least one team before migrating entries."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (legacyActions.isEmpty()) {
                // Empty state - all migrated
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "All entries migrated!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Legacy entries have been automatically assigned to a default player. You can reassign them to specific players from the History screen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${legacyActions.size} legacy ${if (legacyActions.size == 1) "entry" else "entries"} found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "These will be automatically assigned to 'Legacy Player' on next app start. You can manually reassign them to specific players here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Legacy action list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(legacyActions) { action ->
                        MigrationActionCard(
                            action = action,
                            players = players,
                            teams = teams,
                            viewModel = viewModel,
                            onAssign = { selectedPlayerId, selectedTeamId ->
                                viewModel.assignPlayerTeamToAction(
                                    action = action,
                                    playerId = selectedPlayerId,
                                    teamId = selectedTeamId,
                                    context = context
                                )
                            }
                        )
                    }
                    // Add bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a legacy action with player/team assignment dropdowns.
 */
@Composable
private fun MigrationActionCard(
    action: SoccerAction,
    players: List<Player>,
    teams: List<Team>,
    viewModel: SoccerViewModel,
    onAssign: (playerId: String, teamId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlayerId by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf("") }
    var showPlayerDropdown by remember { mutableStateOf(false) }
    var showTeamDropdown by remember { mutableStateOf(false) }

    val canAssign = selectedPlayerId.isNotBlank() && selectedTeamId.isNotBlank()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Action details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${action.getFormattedDate()} at ${action.getFormattedTime()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${action.actionCount} ${action.getActionTypeEnum().displayName()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(
                    onClick = { },
                    label = { Text(if (action.isMatch) "Match" else "Training") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (action.isMatch)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            if (action.opponent.isNotBlank()) {
                Text(
                    text = "vs ${action.opponent}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player selection
            Text(
                text = "Select Player",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box {
                OutlinedButton(
                    onClick = { showPlayerDropdown = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = players.isNotEmpty()
                ) {
                    Text(
                        text = if (selectedPlayerId.isNotBlank()) {
                            viewModel.getPlayerById(selectedPlayerId)?.getDisplayName() ?: "Select Player"
                        } else {
                            "Select Player"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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
                                showPlayerDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Team selection (only if player selected)
            if (selectedPlayerId.isNotBlank()) {
                Text(
                    text = "Select Team",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                val selectedPlayer = viewModel.getPlayerById(selectedPlayerId)
                val playerTeams = selectedPlayer?.teams?.mapNotNull { teamId ->
                    viewModel.getTeamById(teamId)
                } ?: emptyList()

                if (playerTeams.isEmpty()) {
                    Text(
                        text = "This player has no teams assigned. Please assign teams to the player first.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Box {
                        OutlinedButton(
                            onClick = { showTeamDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (selectedTeamId.isNotBlank()) {
                                    viewModel.getTeamById(selectedTeamId)?.getDisplayName() ?: "Select Team"
                                } else {
                                    "Select Team"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Assign button
            Button(
                onClick = {
                    onAssign(selectedPlayerId, selectedTeamId)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canAssign
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assign Player & Team")
            }
        }
    }
}
