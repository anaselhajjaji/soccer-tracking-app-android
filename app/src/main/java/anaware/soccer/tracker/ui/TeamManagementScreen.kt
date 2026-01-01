package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.Team
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Screen for managing teams (CRUD operations).
 * Allows users to add, edit, and delete teams.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    viewModel: SoccerViewModel,
    onNavigateBack: () -> Unit,
    onSetFabAction: ((() -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val teams by viewModel.distinctTeams.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTeam by remember { mutableStateOf<Team?>(null) }
    var deletingTeam by remember { mutableStateOf<Team?>(null) }

    // Register FAB action with parent
    LaunchedEffect(Unit) {
        onSetFabAction?.invoke {
            showAddDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        val padding = PaddingValues(0.dp)
        if (teams.isEmpty()) {
            // Empty state
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No teams yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add your first team",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Team list
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(teams) { team ->
                    TeamCard(
                        team = team,
                        onEdit = { editingTeam = team },
                        onDelete = { deletingTeam = team }
                    )
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingTeam != null) {
        TeamDialog(
            team = editingTeam,
            onDismiss = {
                showAddDialog = false
                editingTeam = null
            },
            onSave = { name, color, league, season ->
                if (editingTeam != null) {
                    // Edit existing team
                    val updatedTeam = editingTeam!!.copy(
                        name = name,
                        color = color,
                        league = league,
                        season = season
                    )
                    viewModel.updateTeam(updatedTeam, context)
                } else {
                    // Add new team
                    viewModel.addTeam(name, color, league, season, context)
                }
                showAddDialog = false
                editingTeam = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingTeam != null) {
        AlertDialog(
            onDismissRequest = { deletingTeam = null },
            title = { Text("Delete Team") },
            text = { Text("Are you sure you want to delete ${deletingTeam!!.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTeam(deletingTeam!!, context)
                        deletingTeam = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTeam = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying team information with edit/delete actions.
 */
@Composable
private fun TeamCard(
    team: Team,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Team name with color indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color indicator
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(parseColor(team.color))
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = team.getDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // League and season
            if (team.league.isNotBlank()) {
                Text(
                    text = "League: ${team.league}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (team.season.isNotBlank()) {
                Text(
                    text = "Season: ${team.season}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (team.league.isBlank() && team.season.isBlank()) {
                Text(
                    text = "No league or season specified",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Dialog for adding or editing a team.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamDialog(
    team: Team?,
    onDismiss: () -> Unit,
    onSave: (name: String, color: String, league: String, season: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(team?.name ?: "") }
    var selectedColor by remember { mutableStateOf(team?.color ?: "#2196F3") }
    var league by remember { mutableStateOf(team?.league ?: "") }
    var season by remember { mutableStateOf(team?.season ?: "") }

    val isValid = name.isNotBlank()

    // Predefined color palette
    val colorPalette = listOf(
        "#2196F3" to "Blue",
        "#F44336" to "Red",
        "#4CAF50" to "Green",
        "#FFC107" to "Yellow",
        "#9C27B0" to "Purple",
        "#FF9800" to "Orange",
        "#00BCD4" to "Cyan",
        "#E91E63" to "Pink",
        "#607D8B" to "Gray"
    )

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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = if (team != null) "Edit Team" else "Add Team",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Team Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color selection
                Text(
                    text = "Team Color",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Color chips in a grid
                Column {
                    colorPalette.chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach { (colorHex, colorName) ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(colorHex))
                                        .border(
                                            width = if (selectedColor == colorHex) 3.dp else 1.dp,
                                            color = if (selectedColor == colorHex) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = colorHex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedColor == colorHex) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // League field (optional)
                OutlinedTextField(
                    value = league,
                    onValueChange = { league = it },
                    label = { Text("League (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Season field (optional)
                OutlinedTextField(
                    value = season,
                    onValueChange = { season = it },
                    label = { Text("Season (optional, e.g., 2024-2025)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            onSave(name, selectedColor, league, season)
                        },
                        enabled = isValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

/**
 * Parses a hex color string to a Compose Color.
 */
private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: IllegalArgumentException) {
        Color(0xFF2196F3) // Default blue
    }
}
