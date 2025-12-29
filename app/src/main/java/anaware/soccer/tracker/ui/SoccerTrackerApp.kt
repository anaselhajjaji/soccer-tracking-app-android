package anaware.soccer.tracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/**
 * Main app composable with hamburger menu navigation and floating action button.
 *
 * Navigation structure:
 * - Hamburger menu (drawer): Chart, History, Account, Management section
 * - Floating action button: Navigate to Add Entry screen
 * - Management submenu: Manage Players, Manage Teams, Manage Matches
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
    val currentRoute = navBackStackEntry?.destination?.route
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch {
                            drawerState.close()
                            navController.navigate(route) {
                                // Avoid building up a large stack
                                popUpTo(Screen.History.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.Add.route -> "Add Entry"
                                Screen.Chart.route -> "Progress Chart"
                                Screen.History.route -> "History"
                                Screen.Backup.route -> "Account"
                                Screen.Players.route -> "Manage Players"
                                Screen.Teams.route -> "Manage Teams"
                                Screen.Matches.route -> "Manage Matches"
                                else -> "Soccer Tracker"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.Add.route) {
                            launchSingleTop = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text("New Action")
                    }
                )
            },
            modifier = modifier
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.History.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Add.route) {
                    AddActionScreen(viewModel = viewModel)
                }
                composable(Screen.Chart.route) {
                    ChartScreen(viewModel = viewModel)
                }
                composable(Screen.History.route) {
                    HistoryScreen(viewModel = viewModel)
                }
                composable(Screen.Backup.route) {
                    BackupScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                composable(Screen.Players.route) {
                    PlayerManagementScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(Screen.Teams.route) {
                    TeamManagementScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(Screen.Matches.route) {
                    MatchManagementScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(Screen.Migration.route) {
                    MigrationScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

/**
 * Drawer content with menu items and management section.
 */
@Composable
private fun DrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Header
    Surface(
        modifier = Modifier.padding(vertical = 16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = "Soccer Tracker",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }

    Divider()

    // Main menu items
    DrawerItem(
        icon = Icons.AutoMirrored.Filled.ShowChart,
        label = "Progress Chart",
        selected = currentRoute == Screen.Chart.route,
        onClick = { onNavigate(Screen.Chart.route) }
    )

    DrawerItem(
        icon = Icons.Default.History,
        label = "History",
        selected = currentRoute == Screen.History.route,
        onClick = { onNavigate(Screen.History.route) }
    )

    DrawerItem(
        icon = Icons.Default.CloudSync,
        label = "Account",
        selected = currentRoute == Screen.Backup.route,
        onClick = { onNavigate(Screen.Backup.route) }
    )

    Divider(modifier = Modifier.padding(vertical = 8.dp))

    // Management section
    Text(
        text = "Management",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    DrawerItem(
        icon = Icons.Default.Person,
        label = "Manage Players",
        selected = currentRoute == Screen.Players.route,
        onClick = { onNavigate(Screen.Players.route) }
    )

    DrawerItem(
        icon = Icons.Default.Groups,
        label = "Manage Teams",
        selected = currentRoute == Screen.Teams.route,
        onClick = { onNavigate(Screen.Teams.route) }
    )

    DrawerItem(
        icon = Icons.Default.SportsScore,
        label = "Manage Matches",
        selected = currentRoute == Screen.Matches.route,
        onClick = { onNavigate(Screen.Matches.route) }
    )
}

/**
 * Individual drawer item.
 */
@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

/**
 * Screen destinations for navigation.
 */
sealed class Screen(val route: String) {
    data object Add : Screen("add")
    data object Chart : Screen("chart")
    data object History : Screen("history")
    data object Backup : Screen("backup")
    data object Players : Screen("players")
    data object Teams : Screen("teams")
    data object Matches : Screen("matches")
    data object Migration : Screen("migration")
}
