package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

    // Player selection
    val players by viewModel.distinctPlayers.collectAsState(initial = emptyList())
    val matches by viewModel.allMatches.collectAsState()
    val teams by viewModel.distinctTeams.collectAsState(initial = emptyList())

    var selectedPlayerId by remember { mutableStateOf("") }
    var showPlayerDropdown by remember { mutableStateOf(false) }

    // Match selection (only for match actions)
    var selectedMatchId by remember { mutableStateOf("") }
    var showMatchDropdown by remember { mutableStateOf(false) }
    var showCreateMatchDialog by remember { mutableStateOf(false) }

    // Team selection (only for training actions)
    var selectedTeamId by remember { mutableStateOf("") }
    var teamDropdownExpanded by remember { mutableStateOf(false) }

    val opponents by viewModel.distinctOpponents.collectAsState(initial = emptyList())

    // Set first player as default when players become available
    LaunchedEffect(players) {
        if (players.isNotEmpty() && selectedPlayerId.isEmpty()) {
            selectedPlayerId = players.first().id
        }
    }

    // Set last match as default when matches become available and session type is Match
    LaunchedEffect(matches, isMatch) {
        if (isMatch && matches.isNotEmpty() && selectedMatchId.isEmpty()) {
            selectedMatchId = matches.sortedByDescending { it.date }.first().id
        }
    }

    // Date and Time state
    var useCurrentDateTime by remember { mutableStateOf(true) }
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

                // Info text about minimum actions
                Text(
                    text = "Tip: At least 1 action is required to save an entry",
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

                // Checkbox for using current date/time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = useCurrentDateTime,
                        onCheckedChange = { useCurrentDateTime = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Use current date & time",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date button
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        enabled = !useCurrentDateTime
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
                        modifier = Modifier.weight(1f),
                        enabled = !useCurrentDateTime
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

        // Match Selection Section (only for match actions)
        if (isMatch) {
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
                        text = "Match",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (matches.isEmpty()) {
                        Text(
                            text = "No matches yet. Create your first match below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    } else {
                        // Match dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { showMatchDropdown = !showMatchDropdown },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedMatchId.isNotEmpty()) {
                                        val match = viewModel.getMatchById(selectedMatchId)
                                        if (match != null) {
                                            val playerTeam = teams.find { it.id == match.playerTeamId }?.name ?: "Unknown"
                                            val opponentTeam = teams.find { it.id == match.opponentTeamId }?.name ?: "Unknown"
                                            "$playerTeam vs $opponentTeam (${match.getFormattedDate()})"
                                        } else {
                                            "Select Match"
                                        }
                                    } else {
                                        "Select Match"
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = showMatchDropdown,
                                onDismissRequest = { showMatchDropdown = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // List existing matches (sorted by date, most recent first)
                                matches.sortedByDescending { it.date }.forEach { match ->
                                    val playerTeam = teams.find { it.id == match.playerTeamId }?.name ?: "Unknown"
                                    val opponentTeam = teams.find { it.id == match.opponentTeamId }?.name ?: "Unknown"
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("$playerTeam vs $opponentTeam")
                                                Text(
                                                    text = match.getFormattedDate(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedMatchId = match.id
                                            showMatchDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Create New Match Button
                    OutlinedButton(
                        onClick = { showCreateMatchDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Match")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Team selection for training sessions
        if (!isMatch) {
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

                    // Team Dropdown
                    ExposedDropdownMenuBox(
                        expanded = teamDropdownExpanded,
                        onExpandedChange = { teamDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (selectedTeamId.isNotEmpty()) {
                                teams.find { it.id == selectedTeamId }?.name ?: "Select Team"
                            } else {
                                "Select Team"
                            },
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Team *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = teamDropdownExpanded)
                            },
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
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .background(
                                                        color = team.getColorInt(),
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(team.name)
                                        }
                                    },
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

            Spacer(modifier = Modifier.height(24.dp))
        }

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
                    text = "Player *",
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

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button validation
        val canSave = actionCount > 0 && selectedPlayerId.isNotEmpty() &&
                (if (isMatch) selectedMatchId.isNotEmpty() else selectedTeamId.isNotEmpty())

        Button(
            onClick = {
                val dateTime = if (useCurrentDateTime) {
                    LocalDateTime.now()
                } else {
                    LocalDateTime.of(selectedDate, selectedTime)
                }

                if (isMatch) {
                    // Match action: use the new method with selected matchId
                    viewModel.addActionWithMatch(
                        actionCount = actionCount,
                        actionType = actionType,
                        dateTime = dateTime,
                        playerId = selectedPlayerId,
                        matchId = selectedMatchId,
                        context = context
                    )
                } else {
                    // Training action: use existing method with teamId
                    viewModel.addAction(
                        actionCount = actionCount,
                        actionType = actionType,
                        isMatch = false,
                        dateTime = dateTime,
                        opponent = opponent,
                        playerId = selectedPlayerId,
                        teamId = selectedTeamId,
                        context = context
                    )
                }
                // Reset form but keep first player and last match as defaults
                actionCount = 0
                actionType = ActionType.default()
                isMatch = true
                selectedDate = LocalDate.now()
                selectedTime = LocalTime.now()
                opponent = ""
                // Keep selectedPlayerId and selectedMatchId (will be maintained by LaunchedEffect)
                showSuccessMessage = true
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Save Entry",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Validation message
        if (!canSave) {
            Text(
                text = when {
                    actionCount == 0 -> "Add at least 1 action to save"
                    selectedPlayerId.isEmpty() -> "Select a player to save"
                    isMatch && selectedMatchId.isEmpty() -> "Select or create a match to save"
                    !isMatch && selectedTeamId.isEmpty() -> "Select a team to save"
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
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

    // Create Match Dialog
    if (showCreateMatchDialog) {
        var newMatchDate by remember { mutableStateOf(LocalDate.now()) }
        var newMatchPlayerTeamId by remember { mutableStateOf("") }
        var newMatchOpponentTeamId by remember { mutableStateOf("") }
        var newMatchLeague by remember { mutableStateOf("") }
        var newMatchIsHome by remember { mutableStateOf(true) }
        var showNewMatchDatePicker by remember { mutableStateOf(false) }
        var newMatchPlayerTeamExpanded by remember { mutableStateOf(false) }
        var newMatchOpponentTeamExpanded by remember { mutableStateOf(false) }

        val isNewMatchValid = newMatchPlayerTeamId.isNotEmpty() && newMatchOpponentTeamId.isNotEmpty()

        AlertDialog(
            onDismissRequest = { showCreateMatchDialog = false }
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
                        text = "Create New Match",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Date picker
                    OutlinedTextField(
                        value = newMatchDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        onValueChange = { },
                        label = { Text("Date *") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showNewMatchDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Player team dropdown
                    ExposedDropdownMenuBox(
                        expanded = newMatchPlayerTeamExpanded,
                        onExpandedChange = { newMatchPlayerTeamExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = teams.find { it.id == newMatchPlayerTeamId }?.name ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Player Team *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = newMatchPlayerTeamExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = newMatchPlayerTeamExpanded,
                            onDismissRequest = { newMatchPlayerTeamExpanded = false }
                        ) {
                            teams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text(team.name) },
                                    onClick = {
                                        newMatchPlayerTeamId = team.id
                                        newMatchPlayerTeamExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Opponent team dropdown
                    ExposedDropdownMenuBox(
                        expanded = newMatchOpponentTeamExpanded,
                        onExpandedChange = { newMatchOpponentTeamExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = teams.find { it.id == newMatchOpponentTeamId }?.name ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Opponent Team *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = newMatchOpponentTeamExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = newMatchOpponentTeamExpanded,
                            onDismissRequest = { newMatchOpponentTeamExpanded = false }
                        ) {
                            teams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text(team.name) },
                                    onClick = {
                                        newMatchOpponentTeamId = team.id
                                        newMatchOpponentTeamExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // League/tournament (optional)
                    OutlinedTextField(
                        value = newMatchLeague,
                        onValueChange = { newMatchLeague = it },
                        label = { Text("League/Tournament (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Home/Away toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = newMatchIsHome,
                            onClick = { newMatchIsHome = true },
                            label = { Text("Home") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !newMatchIsHome,
                            onClick = { newMatchIsHome = false },
                            label = { Text("Away") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCreateMatchDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addMatch(
                                    date = newMatchDate.toString(),
                                    playerTeamId = newMatchPlayerTeamId,
                                    opponentTeamId = newMatchOpponentTeamId,
                                    league = newMatchLeague,
                                    playerScore = -1,
                                    opponentScore = -1,
                                    isHomeMatch = newMatchIsHome,
                                    context = context
                                )
                                // The newly created match will be available after ViewModel updates
                                // and will be auto-selected by the LaunchedEffect
                                showCreateMatchDialog = false
                            },
                            enabled = isNewMatchValid
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }

        // Date picker for new match
        if (showNewMatchDatePicker) {
            val newMatchDatePickerState = rememberDatePickerState(
                initialSelectedDateMillis = newMatchDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showNewMatchDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            newMatchDatePickerState.selectedDateMillis?.let { millis ->
                                newMatchDate = java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showNewMatchDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewMatchDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = newMatchDatePickerState)
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
