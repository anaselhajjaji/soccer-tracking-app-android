package anaware.soccer.tracker.ui

import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.time.format.DateTimeFormatter

/**
 * Screen displaying a progress chart of offensive actions over time.
 * Shows trends and patterns in the player's performance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    viewModel: SoccerViewModel,
    modifier: Modifier = Modifier
) {
    var selectedActionType by remember { mutableStateOf<ActionType?>(ActionType.default()) }
    var showPlayTime by remember { mutableStateOf(false) } // true = show play time chart instead of action counts
    var selectedSessionType by remember {
        mutableStateOf<Boolean?>(
            null
        )
    } // null = both, true = match, false = training
    var selectedOpponentTeamId by remember { mutableStateOf<String?>(null) } // null = all opponent teams
    var selectedPlayerId by remember { mutableStateOf<String?>(null) } // null = all players
    var selectedTeamId by remember { mutableStateOf<String?>(null) } // null = all teams
    var showFilters by remember { mutableStateOf(false) }
    var opponentTeamDropdownExpanded by remember { mutableStateOf(false) }
    var playerTeamDropdownExpanded by remember { mutableStateOf(false) }

    val matches by viewModel.allMatches.collectAsState()
    val players by viewModel.distinctPlayers.collectAsState(initial = emptyList())
    val teams by viewModel.distinctTeams.collectAsState(initial = emptyList())

    val allActions by viewModel.allActions.collectAsState()

    // Get opponent teams from matches
    val opponentTeams = remember(matches, teams) {
        matches.mapNotNull { match ->
            teams.find { it.id == match.opponentTeamId }
        }.distinctBy { it.id }
    }

    val actions = remember(
        allActions,
        selectedActionType,
        showPlayTime,
        selectedSessionType,
        selectedOpponentTeamId,
        selectedPlayerId,
        selectedTeamId
    ) {
        if (!showPlayTime && selectedActionType == null) {
            emptyList()
        } else {
            allActions.filter { action ->
                // For play time mode, filter for time-tracking actions
                // For action count mode, filter for selected action type
                val matchesActionType = if (showPlayTime) {
                    action.getActionTypeEnum().isTimeTracking()
                } else {
                    action.getActionTypeEnum() == selectedActionType
                }

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
    }

    val totalCount = remember(actions, showPlayTime, matches) {
        if (showPlayTime) {
            // Calculate total play time across all matches
            actions.filter { it.matchId.isNotBlank() }
                .groupBy { it.matchId }
                .mapNotNull { (matchId, matchActions) ->
                    val match = matches.find { it.id == matchId }
                    val playerId = matchActions.firstOrNull()?.playerId ?: return@mapNotNull null
                    match?.calculatePlayTime(matchActions, playerId)
                }
                .sum()
        } else {
            actions.sumOf { it.actionCount }
        }
    }

    // Check if any filters are active
    val hasActiveFilters = selectedSessionType != null ||
        selectedOpponentTeamId != null ||
        selectedPlayerId != null ||
        selectedTeamId != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with filter button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress Chart",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            FilledTonalIconButton(
                onClick = { showFilters = !showFilters },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (hasActiveFilters) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Toggle Filters",
                    tint = if (hasActiveFilters) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        // Collapsible Filters Card
        if (showFilters) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Action Type Filter
                    Text(
                        text = "Select Metric",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // First row: Scoring actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionType.scoringActions().forEach { type ->
                            FilterChip(
                                selected = !showPlayTime && selectedActionType == type,
                                onClick = {
                                    showPlayTime = false
                                    selectedActionType = type
                                },
                                label = { Text(type.displayName()) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Second row: Play Time option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = showPlayTime,
                            onClick = {
                                showPlayTime = true
                                selectedActionType = null
                            },
                            label = { Text("Play Time") },
                            modifier = Modifier.weight(1f)
                        )
                        // Empty spacers to maintain layout
                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Session Type Filter
                    Text(
                        text = "Filter by Session Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedSessionType == null,
                            onClick = { selectedSessionType = null },
                            label = { Text("Both") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedSessionType == true,
                            onClick = { selectedSessionType = true },
                            label = { Text("Match") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedSessionType == false,
                            onClick = { selectedSessionType = false },
                            label = { Text("Training") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opponent Team Filter
                    Text(
                        text = "Filter by Opponent",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
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
                        if (opponentTeams.isNotEmpty()) {
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = opponentTeamDropdownExpanded
                                        )
                                    },
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
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Filter by Player",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = selectedPlayerId == null,
                                onClick = { selectedPlayerId = null },
                                label = { Text("All") }
                            )

                            players.take(3).forEach { player ->
                                FilterChip(
                                    selected = selectedPlayerId == player.id,
                                    onClick = {
                                        selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                                    },
                                    label = { Text(player.getDisplayName()) }
                                )
                            }
                        }

                        if (players.size > 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                            var showAllPlayers by remember { mutableStateOf(false) }

                            if (showAllPlayers) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    players.drop(3).forEach { player ->
                                        FilterChip(
                                            selected = selectedPlayerId == player.id,
                                            onClick = {
                                                selectedPlayerId = if (selectedPlayerId == player.id) null else player.id
                                            },
                                            label = { Text(player.getDisplayName()) }
                                        )
                                    }
                                }
                                TextButton(onClick = { showAllPlayers = false }) {
                                    Text("Show Less")
                                }
                            } else {
                                TextButton(onClick = { showAllPlayers = true }) {
                                    Text("Show More (${players.size - 3} more)")
                                }
                            }
                        }
                    }

                    // Team Filter (only show if there are teams)
                    if (teams.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Filter by Team",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
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
                                expanded = playerTeamDropdownExpanded,
                                onExpandedChange = { playerTeamDropdownExpanded = it },
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = playerTeamDropdownExpanded
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = playerTeamDropdownExpanded,
                                    onDismissRequest = { playerTeamDropdownExpanded = false }
                                ) {
                                    teams.forEach { team ->
                                        DropdownMenuItem(
                                            text = { Text(team.getDisplayName()) },
                                            onClick = {
                                                selectedTeamId = team.id
                                                playerTeamDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Clear All Filters Button
                    if (hasActiveFilters) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                selectedSessionType = null
                                selectedOpponentTeamId = null
                                selectedPlayerId = null
                                selectedTeamId = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear All Filters")
                        }
                    }
                }
            }
        }

        // Statistics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (showPlayTime) {
                    // Statistics for time-tracking actions
                    val uniqueDates = actions
                        .filter { it.matchId.isNotBlank() }
                        .map { it.getLocalDateTime().toLocalDate() }
                        .distinct()
                        .size

                    StatisticItem(
                        label = "Total Play Time",
                        value = "$totalCount min"
                    )
                    StatisticItem(
                        label = "Days",
                        value = "$uniqueDates"
                    )
                    StatisticItem(
                        label = "Avg per Day",
                        value = if (uniqueDates > 0) {
                            String.format(java.util.Locale.US, "%.1f min", totalCount.toFloat() / uniqueDates)
                        } else {
                            "0 min"
                        }
                    )
                } else {
                    // Statistics for scoring actions
                    StatisticItem(
                        label = "Total Actions",
                        value = "$totalCount"
                    )
                    StatisticItem(
                        label = "Sessions",
                        value = "${actions.size}"
                    )
                    StatisticItem(
                        label = "Average",
                        value = if (actions.isNotEmpty()) {
                            String.format(java.util.Locale.US, "%.1f", totalCount.toFloat() / actions.size)
                        } else {
                            "0"
                        }
                    )
                }
            }
        }

        // Chart
        if (actions.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "No data to display",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Add entries to see the progress chart",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    ActionProgressChart(
                        actions = actions,
                        showPlayTime = showPlayTime,
                        matches = matches
                    )
                }
            }

            // Chart legend
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About the Chart",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (selectedActionType?.isTimeTracking() == true) {
                            "• Each point represents average play time per day\n" +
                                "• The Y-axis shows play time in minutes\n" +
                                "• The X-axis shows the date\n" +
                                "• If multiple matches occur on the same day, the average is shown"
                        } else {
                            "• Each point represents a training or match session\n" +
                                "• The Y-axis shows the number of offensive actions\n" +
                                "• The X-axis shows the date of each session\n" +
                                "• Track improvement trends over time"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Displays a statistic with label and value.
 */
@Composable
private fun StatisticItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Calculate play time from training session PLAYER_IN/PLAYER_OUT actions.
 * Uses the same pairing algorithm as Match.calculatePlayTime but without requiring a Match entity.
 */
private fun calculateTrainingPlayTime(actions: List<SoccerAction>): Int? {
    // Sort actions by time
    val sortedActions = actions.sortedBy { it.getLocalDateTime() }

    var totalMinutes = 0
    var inTime: java.time.LocalDateTime? = null

    sortedActions.forEach { action ->
        when (action.getActionTypeEnum()) {
            ActionType.PLAYER_IN -> {
                // If already in, ignore (invalid state)
                if (inTime == null) {
                    inTime = action.getLocalDateTime()
                }
            }
            ActionType.PLAYER_OUT -> {
                // If we have a matching IN, calculate duration
                inTime?.let { start ->
                    val end = action.getLocalDateTime()
                    val duration = java.time.Duration.between(start, end)
                    totalMinutes += duration.toMinutes().toInt()
                    inTime = null // Reset for next pair
                }
            }
            else -> {} // Ignore other action types
        }
    }

    return if (totalMinutes > 0) totalMinutes else null
}

/**
 * Line chart component showing action count over time, or play time for time-tracking actions.
 */
@Composable
private fun ActionProgressChart(
    actions: List<SoccerAction>,
    showPlayTime: Boolean,
    matches: List<anaware.soccer.tracker.data.Match>,
    modifier: Modifier = Modifier
) {
    // Create chart entries
    val chartEntries = remember(actions, showPlayTime, matches) {
        if (showPlayTime) {
            // For time-tracking actions, group by date and calculate play time per day
            // Handle both match and training sessions
            val dateToPlayTimes = actions
                .groupBy { it.getLocalDateTime().toLocalDate() }
                .mapValues { (_, actionsForDate) ->
                    // Separate match and training actions
                    val matchActions = actionsForDate.filter { it.matchId.isNotBlank() }
                    val trainingActions = actionsForDate.filter { it.matchId.isBlank() }

                    // Calculate match play times
                    val matchPlayTimes = matchActions
                        .groupBy { it.matchId }
                        .mapNotNull { (matchId, actions) ->
                            val match = matches.find { it.id == matchId }
                            val playerId = actions.firstOrNull()?.playerId ?: return@mapNotNull null
                            match?.calculatePlayTime(actions, playerId)
                        }
                        .filter { it > 0 }

                    // Calculate training play times (group by player since no match)
                    val trainingPlayTimes = trainingActions
                        .groupBy { it.playerId }
                        .mapNotNull { (playerId, actions) ->
                            if (playerId.isBlank()) return@mapNotNull null
                            calculateTrainingPlayTime(actions)
                        }
                        .filter { it > 0 }

                    // Combine both match and training play times
                    matchPlayTimes + trainingPlayTimes
                }
                .filter { it.value.isNotEmpty() }
                .toSortedMap()

            // Calculate average play time per day
            dateToPlayTimes.entries.mapIndexed { index, (_, playTimes) ->
                val averagePlayTime = playTimes.average().toFloat()
                FloatEntry(
                    x = index.toFloat(),
                    y = averagePlayTime
                )
            }
        } else {
            // For scoring actions, show action count
            actions.mapIndexed { index, action ->
                FloatEntry(
                    x = index.toFloat(),
                    y = action.actionCount.toFloat()
                )
            }
        }
    }

    val chartEntryModelProducer = remember(chartEntries) {
        ChartEntryModelProducer(listOf(chartEntries))
    }

    // Format dates for X-axis
    val dateFormatter = remember(actions, showPlayTime, matches) {
        if (showPlayTime) {
            // For time-tracking, we need to map index to dates from grouped data
            // Include both match and training actions
            val dates = actions
                .groupBy { it.getLocalDateTime().toLocalDate() }
                .keys
                .sorted()

            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                val index = value.toInt()
                if (index in dates.indices) {
                    dates.elementAt(index).format(DateTimeFormatter.ofPattern("MM/dd"))
                } else {
                    ""
                }
            }
        } else {
            // For scoring actions, use individual action dates
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                val index = value.toInt()
                if (index in actions.indices) {
                    val dateTime = actions[index].getLocalDateTime()
                    dateTime.format(DateTimeFormatter.ofPattern("MM/dd"))
                } else {
                    ""
                }
            }
        }
    }

    val axisTitle = if (showPlayTime) "Play Time (min)" else "Actions"

    ProvideChartStyle {
        Chart(
            chart = lineChart(
                spacing = 48.dp
            ),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(
                title = axisTitle,
                titleComponent = null
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = dateFormatter,
                guideline = null
            ),
            modifier = modifier.fillMaxSize()
        )
    }
}
