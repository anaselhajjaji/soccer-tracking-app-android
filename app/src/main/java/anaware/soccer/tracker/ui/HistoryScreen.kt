package anaware.soccer.tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import anaware.soccer.tracker.data.ActionType
import anaware.soccer.tracker.data.SoccerAction

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
    val totalCount by viewModel.totalActionCount.collectAsState()
    var actionToDelete by remember { mutableStateOf<SoccerAction?>(null) }

    // Filter states
    var selectedActionType by remember { mutableStateOf<ActionType?>(null) }
    var selectedSessionType by remember { mutableStateOf<Boolean?>(null) }  // null = Both, true = Match, false = Training
    var selectedOpponent by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    // Apply filters
    val filteredActions = remember(allActions, selectedActionType, selectedSessionType, selectedOpponent) {
        allActions.filter { action ->
            val matchesActionType = selectedActionType == null || action.getActionTypeEnum() == selectedActionType
            val matchesSessionType = selectedSessionType == null || action.isMatch == selectedSessionType
            val matchesOpponent = selectedOpponent == null ||
                (selectedOpponent == "No Opponent" && action.opponent.isBlank()) ||
                action.opponent == selectedOpponent
            matchesActionType && matchesSessionType && matchesOpponent
        }
    }

    // Check if any filters are active
    val hasActiveFilters = selectedActionType != null || selectedSessionType != null || selectedOpponent != null

    Column(
        modifier = modifier
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
                            containerColor = if (hasActiveFilters)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (hasActiveFilters)
                                MaterialTheme.colorScheme.onTertiary
                            else
                                MaterialTheme.colorScheme.onSurface
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
                                    selectedOpponent = null
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

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

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

                    // Opponent Filter (only show if there are opponents)
                    if (opponents.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Opponent",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Show "All" and "No Opponent" options
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = selectedOpponent == null,
                                onClick = { selectedOpponent = null },
                                label = { Text("All") }
                            )
                            FilterChip(
                                selected = selectedOpponent == "No Opponent",
                                onClick = {
                                    selectedOpponent = if (selectedOpponent == "No Opponent") null else "No Opponent"
                                },
                                label = { Text("No Opponent") }
                            )
                        }

                        // Show opponent chips - wrap using Column + Row for flow effect
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            opponents.chunked(3).forEach { chunk ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    chunk.forEach { opponent ->
                                        FilterChip(
                                            selected = selectedOpponent == opponent,
                                            onClick = {
                                                selectedOpponent = if (selectedOpponent == opponent) null else opponent
                                            },
                                            label = { Text(opponent) }
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
                        onDeleteClick = { actionToDelete = action }
                    )
                }
            }
        }
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
    onDeleteClick: () -> Unit,
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

                // Action count and type
                Text(
                    text = "${action.actionCount} ${action.getActionTypeEnum().displayName()}${if (action.actionCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

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

                // Opponent if present
                if (action.opponent.isNotBlank()) {
                    Text(
                        text = "vs ${action.opponent}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Delete button
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
