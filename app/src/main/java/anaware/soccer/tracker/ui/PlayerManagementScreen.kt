package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.Player
import anaware.soccer.tracker.data.Team
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen for managing players (CRUD operations).
 * Allows users to add, edit, and delete players.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerManagementScreen(
    viewModel: SoccerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val players by viewModel.distinctPlayers.collectAsState()
    val teams by viewModel.distinctTeams.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPlayer by remember { mutableStateOf<Player?>(null) }
    var deletingPlayer by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Players") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Player")
            }
        }
    ) { padding ->
        if (players.isEmpty()) {
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
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No players yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add your first player",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Player list
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(players) { player ->
                    PlayerCard(
                        player = player,
                        teams = teams,
                        onEdit = { editingPlayer = player },
                        onDelete = { deletingPlayer = player },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingPlayer != null) {
        PlayerDialog(
            player = editingPlayer,
            teams = teams,
            onDismiss = {
                showAddDialog = false
                editingPlayer = null
            },
            onSave = { name, birthdate, number, selectedTeamIds ->
                if (editingPlayer != null) {
                    // Edit existing player
                    val updatedPlayer = editingPlayer!!.copy(
                        name = name,
                        birthdate = birthdate,
                        number = number,
                        teams = selectedTeamIds
                    )
                    viewModel.updatePlayer(updatedPlayer, context)
                } else {
                    // Add new player
                    viewModel.addPlayer(name, birthdate, number, selectedTeamIds, context)
                }
                showAddDialog = false
                editingPlayer = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingPlayer != null) {
        AlertDialog(
            onDismissRequest = { deletingPlayer = null },
            title = { Text("Delete Player") },
            text = { Text("Are you sure you want to delete ${deletingPlayer!!.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlayer(deletingPlayer!!, context)
                        deletingPlayer = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPlayer = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying player information with edit/delete actions.
 */
@Composable
private fun PlayerCard(
    player: Player,
    teams: List<Team>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    viewModel: SoccerViewModel,
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
            // Player name and number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

            // Player details
            Text(
                text = "Age: ${player.getAge()} years",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Text(
                text = "Birthdate: ${player.getFormattedBirthdate()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            // Teams
            if (player.teams.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Teams:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                player.teams.forEach { teamId ->
                    val team = viewModel.getTeamById(teamId)
                    if (team != null) {
                        Text(
                            text = "• ${team.getDisplayName()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No teams assigned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Dialog for adding or editing a player.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerDialog(
    player: Player?,
    teams: List<Team>,
    onDismiss: () -> Unit,
    onSave: (name: String, birthdate: String, number: Int, teamIds: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(player?.name ?: "") }
    var number by remember { mutableStateOf(player?.number?.toString() ?: "") }
    var birthdate by remember { mutableStateOf(player?.birthdate ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTeamIds by remember { mutableStateOf(player?.teams?.toSet() ?: emptySet()) }

    val isValid = name.isNotBlank() && birthdate.isNotBlank()

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
                    text = if (player != null) "Edit Player" else "Add Player",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Number field
                OutlinedTextField(
                    value = number,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            number = it
                        }
                    },
                    label = { Text("Jersey Number (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Birthdate field
                OutlinedTextField(
                    value = if (birthdate.isNotBlank()) {
                        try {
                            val date = LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE)
                            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        } catch (e: Exception) {
                            birthdate
                        }
                    } else {
                        ""
                    },
                    onValueChange = { },
                    label = { Text("Birthdate") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Teams section
                if (teams.isNotEmpty()) {
                    Text(
                        text = "Teams",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    teams.forEach { team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedTeamIds.contains(team.id),
                                onCheckedChange = { checked ->
                                    selectedTeamIds = if (checked) {
                                        selectedTeamIds + team.id
                                    } else {
                                        selectedTeamIds - team.id
                                    }
                                }
                            )
                            Text(
                                text = team.getDisplayName(),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text(
                        text = "No teams available. Add teams first to assign them to players.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
                            val finalNumber = number.toIntOrNull() ?: 0
                            onSave(name, birthdate, finalNumber, selectedTeamIds.toList())
                        },
                        enabled = isValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (birthdate.isNotBlank()) {
                try {
                    val date = LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE)
                    date.toEpochDay() * 86400000
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } else {
                System.currentTimeMillis()
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val date = LocalDate.ofEpochDay(selectedMillis / 86400000)
                            birthdate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
