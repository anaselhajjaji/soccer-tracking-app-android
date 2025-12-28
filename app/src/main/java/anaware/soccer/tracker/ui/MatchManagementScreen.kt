package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.Match
import anaware.soccer.tracker.data.Team
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Screen for managing matches (CRUD operations).
 * Allows users to add, edit, and delete matches.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchManagementScreen(
    viewModel: SoccerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val matches by viewModel.allMatches.collectAsState()
    val teams by viewModel.allTeams.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingMatch by remember { mutableStateOf<Match?>(null) }
    var deletingMatch by remember { mutableStateOf<Match?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Matches") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Match")
            }
        }
    ) { padding ->
        if (matches.isEmpty()) {
            // Empty state
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsScore,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No matches yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add your first match",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Match list
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        teams = teams,
                        viewModel = viewModel,
                        onEdit = { editingMatch = match },
                        onDelete = { deletingMatch = match }
                    )
                }
            }
        }
    }

    // Add/Edit Match Dialog
    if (showAddDialog || editingMatch != null) {
        MatchDialog(
            match = editingMatch,
            teams = teams,
            onDismiss = {
                showAddDialog = false
                editingMatch = null
            },
            onSave = { date, playerTeamId, opponentTeamId, league, playerScore, opponentScore ->
                if (editingMatch != null) {
                    // Edit existing match
                    val updated = editingMatch!!.copy(
                        date = date,
                        playerTeamId = playerTeamId,
                        opponentTeamId = opponentTeamId,
                        league = league,
                        playerScore = playerScore,
                        opponentScore = opponentScore
                    )
                    viewModel.updateMatch(updated, context)
                } else {
                    // Add new match
                    viewModel.addMatch(
                        date = date,
                        playerTeamId = playerTeamId,
                        opponentTeamId = opponentTeamId,
                        league = league,
                        playerScore = playerScore,
                        opponentScore = opponentScore,
                        context = context
                    )
                }
                showAddDialog = false
                editingMatch = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingMatch != null) {
        val matchName = getMatchName(deletingMatch!!, teams)
        AlertDialog(
            onDismissRequest = { deletingMatch = null },
            title = { Text("Delete Match") },
            text = {
                Text("Are you sure you want to delete the match \"$matchName\"? This will clear the match reference from all associated actions.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMatch(deletingMatch!!, context)
                        deletingMatch = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingMatch = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying match information.
 */
@Composable
private fun MatchCard(
    match: Match,
    teams: List<Team>,
    viewModel: SoccerViewModel,
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
            // Header row with match name and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getMatchName(match, teams),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = match.getFormattedDate(),
                        style = MaterialTheme.typography.bodyMedium,
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

            // Score row (if recorded)
            if (match.hasScores()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = match.getScoreDisplay(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    match.getResult()?.let { result ->
                        AssistChip(
                            onClick = { },
                            label = { Text(result.displayName()) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (result) {
                                    anaware.soccer.tracker.data.MatchResult.WIN -> MaterialTheme.colorScheme.primaryContainer
                                    anaware.soccer.tracker.data.MatchResult.LOSS -> MaterialTheme.colorScheme.errorContainer
                                    anaware.soccer.tracker.data.MatchResult.DRAW -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        )
                    }
                }
            }

            // League/tournament (if set)
            if (match.league.isNotBlank()) {
                Text(
                    text = match.league,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Action count
            val actionCount = viewModel.getActionsForMatch(match.id).size
            Text(
                text = "$actionCount action${if (actionCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Dialog for adding or editing a match.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchDialog(
    match: Match?,
    teams: List<Team>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember {
        mutableStateOf(
            if (match != null && match.date.isNotBlank()) {
                LocalDate.parse(match.date)
            } else {
                LocalDate.now()
            }
        )
    }
    var selectedPlayerTeam by remember { mutableStateOf(teams.find { it.id == match?.playerTeamId }) }
    var selectedOpponentTeam by remember { mutableStateOf(teams.find { it.id == match?.opponentTeamId }) }
    var league by remember { mutableStateOf(match?.league ?: "") }
    var playerScore by remember { mutableStateOf(match?.playerScore ?: -1) }
    var opponentScore by remember { mutableStateOf(match?.opponentScore ?: -1) }

    var showDatePicker by remember { mutableStateOf(false) }
    var playerTeamExpanded by remember { mutableStateOf(false) }
    var opponentTeamExpanded by remember { mutableStateOf(false) }

    val isValid = selectedPlayerTeam != null && selectedOpponentTeam != null

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
                    text = if (match != null) "Edit Match" else "Add Match",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Date picker
                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = { },
                    label = { Text("Date *") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Player team dropdown
                ExposedDropdownMenuBox(
                    expanded = playerTeamExpanded,
                    onExpandedChange = { playerTeamExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPlayerTeam?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Player Team *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerTeamExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = playerTeamExpanded,
                        onDismissRequest = { playerTeamExpanded = false }
                    ) {
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name) },
                                onClick = {
                                    selectedPlayerTeam = team
                                    playerTeamExpanded = false
                                }
                            )
                        }
                    }
                }

                // Opponent team dropdown
                ExposedDropdownMenuBox(
                    expanded = opponentTeamExpanded,
                    onExpandedChange = { opponentTeamExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedOpponentTeam?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Opponent Team *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = opponentTeamExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = opponentTeamExpanded,
                        onDismissRequest = { opponentTeamExpanded = false }
                    ) {
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name) },
                                onClick = {
                                    selectedOpponentTeam = team
                                    opponentTeamExpanded = false
                                }
                            )
                        }
                    }
                }

                // League/tournament
                OutlinedTextField(
                    value = league,
                    onValueChange = { league = it },
                    label = { Text("League/Tournament (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Score section
                Text(
                    text = "Match Score (Optional)",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (playerScore >= 0) playerScore.toString() else "",
                        onValueChange = {
                            playerScore = it.toIntOrNull() ?: -1
                        },
                        label = { Text("Player") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.titleLarge
                    )
                    OutlinedTextField(
                        value = if (opponentScore >= 0) opponentScore.toString() else "",
                        onValueChange = {
                            opponentScore = it.toIntOrNull() ?: -1
                        },
                        label = { Text("Opponent") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
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
                            onSave(
                                selectedDate.toString(),
                                selectedPlayerTeam!!.id,
                                selectedOpponentTeam!!.id,
                                league,
                                playerScore,
                                opponentScore
                            )
                        },
                        enabled = isValid
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
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
}

/**
 * Helper function to get match name from team IDs.
 */
private fun getMatchName(match: Match, teams: List<Team>): String {
    val playerTeam = teams.find { it.id == match.playerTeamId }?.name ?: "Unknown"
    val opponentTeam = teams.find { it.id == match.opponentTeamId }?.name ?: "Unknown"
    return "$playerTeam vs $opponentTeam"
}
