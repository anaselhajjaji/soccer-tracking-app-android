package anaware.soccer.tracker.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

/**
 * Screen for backing up and restoring data to/from Firebase Firestore.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: SoccerViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSignedIn by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showManagementMenu by remember { mutableStateOf(false) }

    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    // Check sign-in status
    LaunchedEffect(Unit) {
        val firebaseService = viewModel.getFirebaseService(context)
        isSignedIn = firebaseService.isUserSignedIn()
        userEmail = firebaseService.getCurrentUserEmail()
    }

    // Update isSignedIn based on autoSyncEnabled
    LaunchedEffect(autoSyncEnabled) {
        if (autoSyncEnabled) {
            val firebaseService = viewModel.getFirebaseService(context)
            isSignedIn = firebaseService.isUserSignedIn()
            userEmail = firebaseService.getCurrentUserEmail()
        }
    }

    // Google Sign-In launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    scope.launch {
                        isLoading = true
                        statusMessage = null
                        val firebaseService = viewModel.getFirebaseService(context)
                        val signInResult = firebaseService.signInWithGoogle(idToken)

                        isLoading = false
                        if (signInResult.isSuccess) {
                            isSignedIn = true
                            userEmail = signInResult.getOrNull()
                            statusMessage = "Signed in successfully"
                        } else {
                            isSignedIn = false
                            userEmail = null
                            statusMessage = "Sign-in failed: ${signInResult.exceptionOrNull()?.message}"
                        }
                    }
                } else {
                    isSignedIn = false
                    userEmail = null
                    statusMessage = "Sign-in failed: No ID token received"
                }
            } catch (e: ApiException) {
                isSignedIn = false
                userEmail = null
                statusMessage = "Sign-in failed: ${e.message}"
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            statusMessage = "Sign-in was cancelled"
        } else {
            statusMessage = "Sign-in failed with result code: ${result.resultCode}"
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManagementMenu = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Management Menu")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Header
        Text(
            text = "Account & Sync",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Auto-sync status card
        if (syncStatus != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = syncStatus!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    if (autoSyncEnabled) {
                        TextButton(onClick = { viewModel.clearSyncStatus() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }

        // Sign-in status card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSignedIn) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp),
                    tint = if (isSignedIn) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                if (isSignedIn) {
                    Text(
                        text = "Signed in as:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = userEmail ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (autoSyncEnabled) {
                        Text(
                            text = "✓ Connected to Firebase",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "All data is stored in Firebase and synced automatically",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.signOutFromFirebase(context)
                                isSignedIn = false
                                userEmail = null
                                statusMessage = "Signed out successfully"
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Sign Out")
                    }
                } else {
                    Text(
                        text = "Not signed in",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            val firebaseService = viewModel.getFirebaseService(context)
                            val signInOptions = firebaseService.getGoogleSignInOptions()
                            val signInClient = GoogleSignIn.getClient(context, signInOptions)
                            signInLauncher.launch(signInClient.signInIntent)
                        }
                    ) {
                        Text("Sign in with Google")
                    }
                }
            }
        }

        // Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "All Data Stored in Firebase",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Your soccer tracking data is automatically saved to Firebase Firestore when you add new entries. Data is synced across all your devices using your Google account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (autoSyncEnabled) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓ Connected and Syncing",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Status message
        if (statusMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (statusMessage?.contains("success") == true) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Text(
                    text = statusMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (statusMessage?.contains("success") == true) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        }

        // Management Menu DropdownMenu
        DropdownMenu(
            expanded = showManagementMenu,
            onDismissRequest = { showManagementMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text("Manage Players")
                    }
                },
                onClick = {
                    showManagementMenu = false
                    navController.navigate(Screen.Players.route)
                }
            )
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text("Manage Teams")
                    }
                },
                onClick = {
                    showManagementMenu = false
                    navController.navigate(Screen.Teams.route)
                }
            )
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SportsScore,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text("Manage Matches")
                    }
                },
                onClick = {
                    showManagementMenu = false
                    navController.navigate(Screen.Matches.route)
                }
            )
        }
    }
}
