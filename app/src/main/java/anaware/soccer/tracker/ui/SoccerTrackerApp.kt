package anaware.soccer.tracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Main app composable that sets up navigation and displays the bottom navigation bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoccerTrackerApp(
    viewModel: SoccerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Attempt automatic sign-in on app startup
    LaunchedEffect(Unit) {
        viewModel.attemptAutoSignIn(context)
    }

    // Reload data when app comes to foreground (but not on first composition)
    var isFirstComposition by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Skip first ON_RESUME as LaunchedEffect already handles initial load
                if (isFirstComposition) {
                    isFirstComposition = false
                } else {
                    // Reload data when app resumes from background
                    viewModel.attemptAutoSignIn(context)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Show snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Add.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Add.route) {
                AddActionScreen(viewModel = viewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(viewModel = viewModel)
            }
            composable(Screen.Chart.route) {
                ChartScreen(viewModel = viewModel)
            }
            composable(Screen.Backup.route) {
                BackupScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Screen destinations for navigation.
 */
sealed class Screen(val route: String) {
    data object Add : Screen("add")
    data object History : Screen("history")
    data object Chart : Screen("chart")
    data object Backup : Screen("backup")
}

/**
 * Bottom navigation item data.
 */
private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * List of bottom navigation items.
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Add.route,
        icon = Icons.Default.Add,
        label = "Add"
    ),
    BottomNavItem(
        route = Screen.History.route,
        icon = Icons.Default.History,
        label = "History"
    ),
    BottomNavItem(
        route = Screen.Chart.route,
        icon = Icons.AutoMirrored.Filled.ShowChart,
        label = "Progress"
    ),
    BottomNavItem(
        route = Screen.Backup.route,
        icon = Icons.Default.CloudSync,
        label = "Account"
    )
)
