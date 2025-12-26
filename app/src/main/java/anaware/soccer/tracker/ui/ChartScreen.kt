package anaware.soccer.tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction
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
    var selectedSessionType by remember { mutableStateOf<Boolean?>(null) } // null = both, true = match, false = training
    var selectedOpponent by remember { mutableStateOf<String?>(null) } // null = all opponents

    val opponents by viewModel.distinctOpponents.collectAsState(initial = emptyList())

    val actions by remember(selectedActionType, selectedSessionType, selectedOpponent) {
        when {
            selectedActionType == null -> {
                // No action type selected - return empty flow
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
            selectedActionType != null && selectedSessionType != null && selectedOpponent != null ->
                viewModel.getActionsByTypeSessionAndOpponent(selectedActionType!!, selectedSessionType!!, selectedOpponent!!)
            selectedActionType != null && selectedOpponent != null ->
                viewModel.getActionsByTypeAndOpponent(selectedActionType!!, selectedOpponent!!)
            selectedActionType != null && selectedSessionType != null ->
                viewModel.getActionsByTypeAndSessionType(selectedActionType!!, selectedSessionType!!)
            selectedActionType != null ->
                viewModel.getActionsByType(selectedActionType!!)
            else ->
                kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    val totalCount by remember(selectedActionType, selectedSessionType, selectedOpponent) {
        when {
            selectedActionType == null -> {
                // No action type selected
                kotlinx.coroutines.flow.flowOf(null)
            }
            selectedActionType != null && selectedSessionType != null && selectedOpponent != null ->
                viewModel.getTotalCountByTypeSessionAndOpponent(selectedActionType!!, selectedSessionType!!, selectedOpponent!!)
            selectedActionType != null && selectedOpponent != null ->
                viewModel.getTotalCountByTypeAndOpponent(selectedActionType!!, selectedOpponent!!)
            selectedActionType != null && selectedSessionType != null ->
                viewModel.getTotalCountByTypeAndSessionType(selectedActionType!!, selectedSessionType!!)
            selectedActionType != null ->
                viewModel.getTotalCountByType(selectedActionType!!)
            else ->
                kotlinx.coroutines.flow.flowOf(null)
        }
    }.collectAsState(initial = null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Progress Chart",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filters Card
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
                    text = "Select Action Type",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Individual action type filters (no "All" option)
                    ActionType.all().forEach { type ->
                        FilterChip(
                            selected = selectedActionType == type,
                            onClick = { selectedActionType = type },
                            label = { Text(type.displayName()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
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

                // Opponent Filter
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
                        selected = selectedOpponent == null,
                        onClick = { selectedOpponent = null },
                        label = { Text("All") }
                    )

                    // Show opponent chips if there are any
                    opponents.take(3).forEach { opponent ->
                        FilterChip(
                            selected = selectedOpponent == opponent,
                            onClick = { selectedOpponent = opponent },
                            label = { Text(opponent) }
                        )
                    }
                }

                // Show more opponents if there are more than 3
                if (opponents.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    var showAllOpponents by remember { mutableStateOf(false) }

                    if (showAllOpponents) {
                        // Show all remaining opponents
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            opponents.drop(3).forEach { opponent ->
                                FilterChip(
                                    selected = selectedOpponent == opponent,
                                    onClick = { selectedOpponent = opponent },
                                    label = { Text(opponent) }
                                )
                            }
                        }
                        TextButton(onClick = { showAllOpponents = false }) {
                            Text("Show Less")
                        }
                    } else {
                        TextButton(onClick = { showAllOpponents = true }) {
                            Text("Show More (${opponents.size - 3} more)")
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
                StatisticItem(
                    label = "Total Actions",
                    value = "${totalCount ?: 0}"
                )
                StatisticItem(
                    label = "Sessions",
                    value = "${actions.size}"
                )
                StatisticItem(
                    label = "Average",
                    value = if (actions.isNotEmpty()) {
                        String.format("%.1f", (totalCount ?: 0).toFloat() / actions.size)
                    } else {
                        "0"
                    }
                )
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
                            imageVector = Icons.Default.ShowChart,
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
                    ActionProgressChart(actions = actions)
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
                        text = "• Each point represents a training or match session\n" +
                                "• The Y-axis shows the number of offensive actions\n" +
                                "• The X-axis shows the date of each session\n" +
                                "• Track improvement trends over time",
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
 * Line chart component showing action count over time.
 */
@Composable
private fun ActionProgressChart(
    actions: List<SoccerAction>,
    modifier: Modifier = Modifier
) {
    // Create chart entries
    val chartEntries = remember(actions) {
        actions.mapIndexed { index, action ->
            FloatEntry(
                x = index.toFloat(),
                y = action.actionCount.toFloat()
            )
        }
    }

    val chartEntryModelProducer = remember(chartEntries) {
        ChartEntryModelProducer(listOf(chartEntries))
    }

    // Format dates for X-axis
    val dateFormatter = remember(actions) {
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

    ProvideChartStyle {
        Chart(
            chart = lineChart(
                spacing = 48.dp
            ),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(
                title = "Actions",
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
