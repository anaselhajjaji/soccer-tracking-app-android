package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Screen for adding new soccer action entries.
 * Allows user to input action count, session type (match/training), custom date/time, and optional notes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(
    viewModel: SoccerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var actionCount by remember { mutableIntStateOf(0) }
    var actionType by remember { mutableStateOf(ActionType.default()) }
    var isMatch by remember { mutableStateOf(true) }
    var opponent by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showOpponentSuggestions by remember { mutableStateOf(false) }

    // Player and Team selection
    var selectedPlayerId by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf("") }
    var showPlayerDropdown by remember { mutableStateOf(false) }
    var showTeamDropdown by remember { mutableStateOf(false) }

    val opponents by viewModel.distinctOpponents.collectAsState(initial = emptyList())
    val players by viewModel.distinctPlayers.collectAsState(initial = emptyList())
    val teams by viewModel.distinctTeams.collectAsState(initial = emptyList())

    // Date and Time state
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = true
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Record Offensive Actions",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Action Count Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Number of Actions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Large action count display
                Text(
                    text = actionCount.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Plus/Minus buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { if (actionCount > 0) actionCount-- },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease count"
                        )
                    }

                    FilledIconButton(
                        onClick = { actionCount++ },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase count"
                        )
                    }
                }

                // Info text about zero actions
                Text(
                    text = "Tip: You can save with 0 actions to track participation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Date and Time Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Date & Time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date button
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                    }

                    // Time button
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Type Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Action Type",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionType.all().forEach { type ->
                        FilterChip(
                            selected = actionType == type,
                            onClick = { actionType = type },
                            label = { Text(type.displayName()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Session Type Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Session Type",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Opponent Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Opponent (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Calculate filtered opponents
                val filteredOpponents = opponents.filter {
                    it.contains(opponent, ignoreCase = true) && it != opponent
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = opponent,
                        onValueChange = { newValue ->
                            opponent = newValue
                            showOpponentSuggestions = newValue.isNotEmpty() && opponents.isNotEmpty()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter opponent name...") },
                        singleLine = true
                    )

                    // Show dropdown only if there are filtered suggestions
                    DropdownMenu(
                        expanded = showOpponentSuggestions && filteredOpponents.isNotEmpty(),
                        onDismissRequest = { showOpponentSuggestions = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredOpponents.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    opponent = suggestion
                                    showOpponentSuggestions = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Player Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Player (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (players.isEmpty()) {
                    Text(
                        text = "No players yet. Add players from the Account tab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showPlayerDropdown = !showPlayerDropdown },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (selectedPlayerId.isNotEmpty()) {
                                    viewModel.getPlayerById(selectedPlayerId)?.getDisplayName() ?: "Select Player"
                                } else {
                                    "Select Player"
                                }
                            )
                        }

                        DropdownMenu(
                            expanded = showPlayerDropdown,
                            onDismissRequest = { showPlayerDropdown = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    selectedPlayerId = ""
                                    selectedTeamId = ""
                                    showPlayerDropdown = false
                                }
                            )
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
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Team Selection (only if player selected)
        if (selectedPlayerId.isNotEmpty()) {
            val selectedPlayer = viewModel.getPlayerById(selectedPlayerId)
            val playerTeams = selectedPlayer?.teams?.mapNotNull { teamId ->
                viewModel.getTeamById(teamId)
            } ?: emptyList()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Team",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (playerTeams.isEmpty()) {
                        Text(
                            text = "This player has no teams. Edit player to add teams.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { showTeamDropdown = !showTeamDropdown },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedTeamId.isNotEmpty()) {
                                        viewModel.getTeamById(selectedTeamId)?.getDisplayName() ?: "Select Team"
                                    } else {
                                        "Select Team"
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = showTeamDropdown,
                                onDismissRequest = { showTeamDropdown = false },
                                modifier = Modifier.fillMaxWidth()
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
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button (now enabled even with 0 actions)
        Button(
            onClick = {
                val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                viewModel.addAction(
                    actionCount = actionCount,
                    actionType = actionType,
                    isMatch = isMatch,
                    dateTime = dateTime,
                    opponent = opponent,
                    playerId = selectedPlayerId,
                    teamId = selectedTeamId,
                    context = context
                )
                // Reset form
                actionCount = 0
                actionType = ActionType.default()
                isMatch = true
                selectedDate = LocalDate.now()
                selectedTime = LocalTime.now()
                opponent = ""
                selectedPlayerId = ""
                selectedTeamId = ""
                showSuccessMessage = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Save Entry",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Success message
        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = false
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = "✓ Entry saved successfully!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()
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

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
