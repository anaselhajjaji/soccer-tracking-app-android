# Claude AI Development Notes

This document contains detailed context and notes from the AI-assisted development of the Soccer Tracker application.

## Project Overview

**Purpose**: Track offensive soccer actions (goals, assists, general offensive plays) for a youth soccer player during matches and training sessions.

**Development Assistant**: Claude Sonnet 4.5 (Anthropic)

**Development Timeline**: December 2025

**Project Type**: Personal Android application for parent/child soccer tracking

## Development History

### v1.0 - Initial Release (December 2025)

The user requested an Android app with the following features:

1. **Action Tracking**: Record offensive actions with specific types:
   - Goals
   - Assists
   - General Offensive Actions
2. **Action Counts**: Track number of each action type per session
3. **Session Types**: Distinguish between match and training sessions
4. **Opponent Tracking**: Record opponent name with autocomplete for previously entered teams
5. **Custom Date/Time**: Choose when the action occurred (not just current time)
6. **Zero Actions Support**: Save entries with 0 actions to track participation without scoring
7. **History View**: Display all entries with advanced filtering capabilities
   - Filter by action type (All, Goals, Assists, Offensive Actions)
   - Filter by session type (Both, Match, Training)
   - Filter by opponent (All, No Opponent, or specific opponent)
   - Combine filters for precise searches
8. **Progress Charts**: Visualize performance over time with triple filtering
   - Select action type (required): Goals, Assists, or Offensive Actions
   - Filter by session type: Both, Match, or Training
   - Filter by opponent: All or specific opponent
9. **Firebase Cloud Storage**: Direct cloud storage with automatic sync
   - Automatic sign-in on app startup
   - Data saved directly to Firebase Firestore
   - Cross-device synchronization
10. **Data Management**: Delete individual entries if mistakes are made

**Technology Choices Made**:

- **Kotlin 2.1.0** - Modern Android development language
- **Jetpack Compose** - Declarative UI framework with Compose BOM 2025.01.00
- **Firebase Firestore** - Cloud NoSQL database for primary data storage
- **Firebase Authentication** - Google Sign-In for user authentication
- **MVVM Architecture** - Separation of concerns, testability
- **Material Design 3** - Modern design system with soccer-themed colors
- **Vico Charts** - Interactive chart library for data visualization
- **kotlinx.serialization** - JSON data format for Firebase integration
- **Android Gradle Plugin 8.9.1** - Latest build tooling
- **Gradle 8.12** - Build system

### v1.1 - Platform Updates & Quality Integration (December 2025)

Major platform upgrade to latest Android and Kotlin tooling, plus comprehensive static analysis integration:

1. **Platform Updates**:
   - Upgraded Android Gradle Plugin from 8.3.0 to 8.9.1
   - Upgraded Kotlin from 1.9.22 to 2.1.0
   - Added Compose Compiler Plugin 2.1.0 (required for Kotlin 2.0+)
   - Upgraded Gradle wrapper from 8.5 to 8.12
   - Updated compileSdk and targetSdk from 34 to 35 (Android 15)

2. **Dependency Updates**:
   - Compose BOM: 2023.10.01 → 2025.01.00
   - Core KTX: 1.12.0 → 1.15.0
   - Lifecycle: 2.6.2 → 2.9.0
   - Activity Compose: 1.8.1 → 1.10.0
   - Navigation: 2.8.0 → 2.9.0
   - Firebase BOM: 33.6.0 → 33.8.0
   - Desugaring: 2.0.4 → 2.1.5
   - Play Services Auth: 20.7.0 → 21.4.0

3. **Code Modernization**:
   - Fixed all Compose deprecations from BOM update:
     - `Icons.Filled.ShowChart` → `Icons.AutoMirrored.Filled.ShowChart`
     - `Divider()` → `HorizontalDivider()`
     - Updated `LocalLifecycleOwner` import to `androidx.lifecycle.compose` package
     - Removed deprecated `window.statusBarColor` usage
   - Added monochrome launcher icons for Android 13+ themed icons
   - Added `.kotlin/` to .gitignore for compiler artifacts

4. **Detekt Integration**:
   - Added Detekt 1.23.4 for Kotlin-specific static analysis
   - Created comprehensive `detekt.yml` configuration with Android-specific rules
   - Enabled auto-correction for formatting issues
   - Configured to generate HTML reports without failing builds
   - Integrated into CI/CD pipeline (GitHub Actions)

5. **Quality Reports**:
   - Created [QUALITY_REPORTS.md](QUALITY_REPORTS.md) documentation
   - Four report types: Detekt, JaCoCo coverage, Android Lint, Test results
   - All reports available locally and as GitHub Actions artifacts
   - Updated `quality-check.sh` script to include Detekt

6. **Auto-Fix Results**:
   - **121 issues automatically corrected** (74% reduction from 164 to 43)
   - Fixed formatting: imports, whitespace, blank lines, indentation
   - Remaining 43 issues are architectural (informational only):
     - 21 wildcard imports (common in Compose projects)
     - 8 generic exception catches in FirebaseService
     - 6 long/complex Compose UI functions
     - 3 line length violations
     - 5 other minor issues

7. **Quality Improvements**:
   - Reduced lint warnings from 18 to 8 (56% reduction)
   - Remaining warnings are for dependencies requiring Android SDK 36 (not yet released)
   - All 55 unit tests pass
   - All 9 UI tests pass on Firebase Test Lab
   - Debug build compiles successfully
   - No breaking changes from formatting corrections

**Rationale for Updates**:
- Stay current with latest Android platform features
- Leverage Kotlin 2.1.0 performance improvements
- Use latest Compose BOM with bug fixes and new components
- User wanted local static analysis reports without cloud services
- SonarQube requires server infrastructure (rejected)
- Detekt generates standalone HTML reports
- Kotlin-specific rules better suited for Android development
- Auto-correction saves manual formatting effort
- Integrates seamlessly with Gradle and GitHub Actions

## Architecture Decisions

### MVVM Pattern Selection

**Choice**: Model-View-ViewModel architecture

**Rationale**:
- **Separation of Concerns**: Clear boundaries between UI, business logic, and data
- **Testability**: ViewModel can be unit tested without UI
- **Lifecycle Awareness**: ViewModel survives configuration changes (rotation)
- **Reactive Updates**: StateFlow automatically updates UI when data changes
- **Android Best Practice**: Recommended by Google for Android apps

**Implementation**:

- **Model**: Data classes and Firebase models
- **View**: Jetpack Compose screens
- **ViewModel**: State management with StateFlow and Firebase integration

### Cloud-First Storage Architecture

**Storage Strategy**: Firebase Firestore as primary backend

**Design Decision**:
- **Primary Storage**: Firebase Firestore (cloud NoSQL backend)
- **Local Cache**: In-memory StateFlow for instant UI updates
- **Sync Strategy**: Optimistic updates with immediate UI feedback
- **Authentication**: Firebase Auth with Google Sign-In
- **Data Isolation**: User-scoped data at `/users/{userId}/actions/{actionId}`

**Benefits**:
- ✅ Automatic cross-device synchronization
- ✅ No manual backup needed
- ✅ Real-time data access from any device
- ✅ Simpler user experience
- ✅ Scalable cloud infrastructure

**Tradeoffs**:
- ⚠️ Requires internet connection for all operations
- ⚠️ No offline mode (future enhancement opportunity)
- ⚠️ Depends on Firebase service availability

### Database Schema

#### Firebase Firestore Structure

```text
/users
  /{userId}
    /actions
      /{timestamp}
        - dateTime: String (ISO-8601)
        - actionCount: Int
        - actionType: String (GOAL, ASSIST, OFFENSIVE_ACTION)
        - match: Boolean (true = match, false = training)
        - opponent: String (optional)
```

#### Data Model

```kotlin
data class SoccerAction(
    val id: Long = 0,              // Timestamp-based unique ID
    val dateTime: String,          // ISO-8601 format
    val actionCount: Int,          // Number of actions (0+)
    val actionType: String,        // "GOAL", "ASSIST", "OFFENSIVE_ACTION"
    val isMatch: Boolean,          // true = match, false = training
    val opponent: String = ""      // Opponent name (optional)
)
```

**Design Decisions**:

1. **Action Type as String**: Store as string instead of int for readability
   - **Pro**: More readable in database browser
   - **Pro**: Easier to debug
   - **Pro**: Self-documenting code
   - **Solution**: Enum provides type safety in code, string provides clarity in database

2. **Opponent Field with Autocomplete**: Structured data for analytics
   - **Rationale**: Enable opponent-specific filtering and head-to-head comparisons
   - **Benefit**: Autocomplete improves data consistency (avoids typos)
   - **Implementation**: `ExposedDropdownMenuBox` with dynamic filtering

3. **Firestore Field Naming**: Boolean stored as `match` not `isMatch`
   - **Requirement**: Firestore requires exact field name matching
   - **Pattern**: Boolean properties stored without "is" prefix
   - **Solution**: Convert between `isMatch` (app) and `match` (Firestore) in serialization layer

4. **Zero Actions Support**: Allow entries with 0 actions
   - **Use Case**: Track participation without scoring
   - **Implementation**: Removed `actionCount > 0` validation
   - **Benefit**: Complete session tracking

### Firebase Service Design

**FirebaseService.kt - CRUD Operations**:

**Key Methods**:
- `silentSignIn()`: Automatic authentication using cached credentials
- `addAction(action)`: Store individual action at `/users/{userId}/actions/{actionId}`
- `deleteAction(actionId)`: Delete individual action from Firestore
- `getAllActions()`: Retrieve all actions for current user
- `listenToActions(onUpdate, onError)`: Real-time listener (available for future features)

**Static Methods**:
- `generateActionId()`: Returns `System.currentTimeMillis()` for unique document IDs

**Data Structure Benefits**:
- One document per action for incremental updates
- Better scalability than single-document approach
- Easier to query and filter
- Supports real-time listeners

**Security**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### State Management Pattern

**ViewModel State Management**:

```kotlin
// In-memory state for immediate UI updates
private val _allActions = MutableStateFlow<List<SoccerAction>>(emptyList())
val allActions: StateFlow<List<SoccerAction>> = _allActions.asStateFlow()

// Filtered views using Flow.map
val goalActions = _allActions.map { actions ->
    actions.filter { it.getActionTypeEnum() == ActionType.GOAL }
}
```

**Optimistic Update Pattern**:
```kotlin
fun addAction(..., context: Context? = null) {
    viewModelScope.launch {
        val result = service.addAction(action)
        if (result.isSuccess) {
            // Update local state immediately for instant UI feedback
            _allActions.value = (_allActions.value + action).sortedByDescending { it.dateTime }
        } else {
            // Show error message if Firebase operation fails
            _uiState.value = _uiState.value.copy(message = "Error: ${result.exceptionOrNull()?.message}")
        }
    }
}
```

**Benefits**:
- Instant UI updates before cloud sync completes
- Error handling for failed operations
- Users see changes immediately

### Critical Technical Issues Solved

#### Issue 1: Firestore Deserialization Requirements

**Problem**: Firestore requires no-argument constructors to deserialize objects

**Solution**: Add default values to all data class parameters
```kotlin
data class BackupAction(
    val dateTime: String = "",       // Default value creates no-arg constructor
    val actionCount: Int = 0,
    val actionType: String = "",
    val match: Boolean = false,      // Firestore field name (not isMatch)
    val opponent: String = ""
)
```

**Technical Insight**:
- Firestore's automatic deserialization requires no-arg constructors
- Property names must match Firestore field names exactly
- Boolean properties stored without "is" prefix

#### Issue 2: ExposedDropdownMenuBox State Management

**Problem**: Autocomplete dropdown interfered with text editing (couldn't delete first character)

**Root Cause**: Expanded state was checking original list instead of filtered results

**Solution**:
```kotlin
// Calculate filtered list before the component
val filteredOpponents = opponents.filter {
    it.contains(opponent, ignoreCase = true) && it != opponent
}

// Use filtered list for expanded state
ExposedDropdownMenuBox(
    expanded = showOpponentSuggestions && filteredOpponents.isNotEmpty(),
    onExpandedChange = {
        // Only allow closing, not opening (controlled by text input)
        if (!it) showOpponentSuggestions = false
    }
) { ... }
```

**Key Learning**: Separate filtering logic from component state management

#### Issue 3: Method Naming for Property Conversions

**Pattern**: When data classes have properties that need type conversion methods

**Example**:
```kotlin
data class SoccerAction(
    val actionType: String,    // Stored as string
) {
    fun getActionTypeEnum(): ActionType {
        return ActionType.valueOf(actionType)
    }
}
```

**Best Practice**: Use descriptive method names like `getActionTypeEnum()` instead of generic names that might conflict with auto-generated getters

## Code Quality & Best Practices

### Input Validation

**Action Entry Validation**:
- Action count can be 0 or positive (zero allowed for participation tracking)
- Action type selected from enum (no invalid values possible)
- Session type is boolean toggle (always valid)
- Opponent field accepts any string (autocomplete helps consistency)

**Firebase Validation**:
- Firebase enforces document structure
- Non-null fields validated by Kotlin compiler
- ISO-8601 datetime format validated at creation

### Error Handling Strategy

**Firebase Operations**:
```kotlin
fun addAction(...) {
    viewModelScope.launch {
        try {
            val action = SoccerAction.create(...)
            val result = service.addAction(action)
            if (result.isSuccess) {
                _allActions.value = (_allActions.value + action).sortedByDescending { it.dateTime }
                _uiState.value = _uiState.value.copy(message = "Action saved successfully")
            } else {
                _uiState.value = _uiState.value.copy(message = "Error: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(message = "Error: ${e.message}")
        }
    }
}
```

**Design Decision**: Graceful degradation
- Errors don't crash app
- User-friendly messages shown
- Operations can be retried

### StateFlow for Reactive UI

**Pattern Used Throughout App**:
```kotlin
// In ViewModel
private val _allActions = MutableStateFlow<List<SoccerAction>>(emptyList())
val allActions: StateFlow<List<SoccerAction>> = _allActions.asStateFlow()

// In Composable
val actions by viewModel.allActions.collectAsState()
```

**Benefits**:
- UI automatically updates when data changes
- No manual refresh needed
- Lifecycle-aware (stops collecting when not visible)
- Initial value prevents null checks

## UI/UX Design Decisions

### Navigation Structure

**Bottom Navigation Bar** (4 tabs):
1. **Add** (Home) - Primary action, most frequent use
2. **History** - View and manage entries with advanced filters
3. **Progress** - Charts and analytics with triple filtering
4. **Account** - Sign-in status and sync information

**Design Decision**: 4 tabs is maximum for bottom navigation
- **Rationale**: More than 4 gets cramped on small screens
- **User Feedback**: Quick access to all features important for frequent use

### History Screen Filters

**Collapsible Filter Panel**:
- Filter icon button in header (highlights when filters are active)
- Expandable/collapsible filter section
- Shows filtered entry count in real-time

**Three Independent Filters**:
1. **Action Type**: All, Goals, Assists, Offensive Actions
2. **Session Type**: Both, Match, Training
3. **Opponent**: All, No Opponent, or specific opponent chips

**Design Decisions**:
- **Collapsible by Default**: Avoids cluttering screen, easily accessible
- **Visual Feedback**: Filter button changes color when active
- **No "All" for Opponents**: Dynamic list, user-defined names
- **"No Opponent" Option**: Find incomplete entries
- **Real-Time Filtering**: No "Apply" button needed
- **Independent Filters**: All optional, can be combined

### Chart Screen Filtering

**Triple Filtering System**:
1. **Action Type** (required): Must select Goal, Assist, or Offensive Action
2. **Session Type** (optional): Both, Match, or Training
3. **Opponent** (optional): All or specific opponent

**Design Decisions**:
- **Required Action Type**: Forces users to select specific metric for meaningful analysis
- **Progressive Disclosure**: "Show More" for opponent lists (first 3 visible)
- **Statistics Card**: Shows total actions, session count, and averages
- **Opponent Chips**: Individual chips for each unique opponent
- **Flexible Combinations**: Any combination of filters for granular insights

### Material 3 Components Used

**Key UI Components**:
- `FilterChip` - Action type and session type filters with selected state
- `ExposedDropdownMenuBox` - Opponent autocomplete with dynamic filtering
- `DatePickerDialog` - Calendar interface for date selection
- `TimePicker` - Clock interface for time selection (24-hour format)
- `Card` with `surfaceVariant` - Filter panels and data cards
- `FilledTonalIconButton` - Filter toggle with state indication
- `FloatingActionButton` - Quick actions (delete entries)

## Performance Considerations

### Firebase Query Optimization

**Current Queries**:
- `getAllActions()`: Fetches all user actions (small dataset expected)
- Document ID as timestamp: Natural chronological ordering
- No pagination needed for personal use case

**Future Optimization Opportunities** (if dataset grows):
- Pagination with Firestore's `startAfter()` and `limit()`
- Date range filtering with `where()` queries
- Real-time listeners for live updates
- Local caching for offline mode support

### Memory Management

**Compose Optimization**:
```kotlin
val filteredActions = remember(allActions, selectedActionType, selectedSessionType, selectedOpponent) {
    // Only recomputed when dependencies change
    allActions.filter { action ->
        val matchesActionType = selectedActionType == null || action.getActionTypeEnum() == selectedActionType
        val matchesSessionType = selectedSessionType == null || action.isMatch == selectedSessionType
        val matchesOpponent = selectedOpponent == null ||
            (selectedOpponent == "No Opponent" && action.opponent.isBlank()) ||
            action.opponent == selectedOpponent
        matchesActionType && matchesSessionType && matchesOpponent
    }
}
```

**Benefits**:
- Minimizes unnecessary recompositions
- Efficient filtering only when dependencies change
- Prevents memory leaks

## Testing Strategy

### Manual Testing Performed

**Build Tests**:
- ✅ Clean build compiles without errors
- ✅ Gradle sync succeeds
- ✅ APK installs on device
- ✅ App launches successfully

**Functional Tests** (performed on emulator and physical device):
- ✅ Add entries with all action types
- ✅ View history with color-coded badges
- ✅ History filters work correctly (all combinations)
- ✅ Delete individual entries
- ✅ Chart displays correctly with triple filtering
- ✅ Opponent autocomplete works smoothly
- ✅ Google Sign-In flow
- ✅ Automatic sign-in on app startup
- ✅ Data saves to Firebase and loads on restart
- ✅ Custom date/time selection
- ✅ Zero action entries
- ✅ Sign out and account switching
- ✅ Rotation preserves state

### Automated Testing

**Unit Tests** (86 tests, 41% overall coverage):

**Test Coverage by Package**:
- ✅ Data models: 94% coverage (SoccerAction, ActionType, BackupData)
  - [SoccerActionTest.kt](app/src/test/java/anaware/soccer/tracker/data/SoccerActionTest.kt): 30 tests
  - Tests data class behavior, formatting methods, edge cases
- ✅ ViewModel: 77% coverage (business logic with comprehensive filtering tests)
  - [SoccerViewModelTest.kt](app/src/test/java/anaware/soccer/tracker/ui/SoccerViewModelTest.kt): 46 tests
  - Tests all filtering methods, StateFlow initialization, state management
- ✅ UI State: 100% coverage
  - [UiStateTest.kt](app/src/test/java/anaware/soccer/tracker/ui/UiStateTest.kt): 10 tests
  - Tests data class behavior, copy functionality, edge cases
- ⚠️ Firebase service: 0% coverage (only static methods tested, Firebase operations need mocking)

**Test Location**: `app/src/test/java/anaware/soccer/tracker/`

**Coverage Configuration**:
- JaCoCo excludes Compose UI screens (AddActionScreen, BackupScreen, ChartScreen, HistoryScreen, SoccerTrackerApp)
- JaCoCo excludes Compose-generated synthetic classes (ComposableSingletons$*.*) added in v1.1
- Includes ViewModel (testable business logic)
- Excludes theme files and MainActivity (framework code)
- UI screens tested separately via instrumentation tests on Firebase Test Lab

**Coverage Improvement** (December 2025):
- Increased test count from 55 to 86 tests (+31 tests, 56% increase)
- Improved overall coverage from 22% to 41% (+19 points, 86% increase)
- Fixed JaCoCo exclusions to properly filter Kotlin/Compose compiler-generated synthetic classes
- Coverage now accurately reflects testable business logic without inflated instruction counts

**UI Tests** (9 tests on Firebase Test Lab):
   - Compose UI interactions
   - Navigation flows (between all 4 tabs)
   - Action count increment/decrement
   - Action type and session type selection
   - Filter button existence
   - Screen element verification

**Test Location**: `app/src/androidTest/java/anaware/soccer/tracker/SoccerTrackerAppTest.kt`

**Test Count**: 9 UI tests covering:

- App launch and navigation bar
- Tab switching (Add, History, Progress, Account)
- Add screen form fields
- Counter controls (+/- buttons)
- Action type selection (Goal, Assist, Offensive Action)
- Session type toggle (Match/Training)
- History screen empty state
- Progress chart filter chips

## Build Configuration

### Gradle Versions

**Configuration** (Updated December 2025):
```kotlin
// Root build.gradle.kts
AGP: 8.9.1
Kotlin: 2.1.0
Gradle Wrapper: 8.12
Compose Compiler Plugin: 2.1.0 (required for Kotlin 2.0+)

// app/build.gradle.kts
compileSdk / targetSdk: 35 (Android 15)
Compose BOM: 2025.01.00
kotlinx-serialization: 1.7.3
Firebase BOM: 33.8.0
```

### Key Dependencies

```kotlin
// Core
implementation("androidx.core:core-ktx:1.15.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
implementation("androidx.activity:activity-compose:1.10.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2025.01.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// Navigation
implementation("androidx.navigation:navigation-compose:2.9.0")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.android.gms:play-services-auth:21.4.0")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

// Charts
implementation("com.patrykandpatrick.vico:compose:1.13.1")
implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
implementation("com.patrykandpatrick.vico:core:1.13.1")

// Desugaring (for LocalDateTime on API 24+)
coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
```

## Continuous Integration

### GitHub Actions Workflow

The project uses GitHub Actions for automated CI/CD pipeline.

**Workflow File**: [`.github/workflows/android-build.yml`](.github/workflows/android-build.yml)

**Triggers**:

- Push to `main` or `master` branches
- Pull requests targeting `main` or `master`

**Pipeline Jobs**:

#### Job 1: build-and-test (runs on all pushes and PRs)

1. **Checkout**: Uses `actions/checkout@v4` to fetch repository code
2. **Java Setup**: Configures JDK 17 (Temurin) with Gradle caching via `actions/setup-java@v4`
3. **Permissions**: Grants execute permission to `gradlew` wrapper
4. **Firebase Config**: Creates `google-services.json` from `GOOGLE_SERVICES_JSON` secret
5. **Debug Keystore**: Creates debug keystore from base64-encoded `DEBUG_KEYSTORE` secret
6. **Build**: Compiles debug APK with `./gradlew assembleDebug`
7. **Unit Tests**: Runs all unit tests with `./gradlew test` (86 tests across 6 files)
8. **Lint**: Performs static code analysis with `./gradlew lintDebug`
9. **Build Test APK**: Compiles instrumentation test APK with `./gradlew assembleDebugAndroidTest`
10. **Artifacts**: Uploads debug APK, test APK, and lint HTML report (7-day retention)

#### Job 2: firebase-test-lab (runs only on push to main/master)

**Trigger**: Only executes on push events to `main` or `master` branches (not on PRs)

**Steps**:

1. **Download APKs**: Retrieves debug and test APKs from previous job
2. **Authenticate**: Uses Google Cloud service account via `google-github-actions/auth@v2`
3. **Setup gcloud**: Configures Google Cloud SDK via `google-github-actions/setup-gcloud@v2`
4. **Set Project**: Sets GCloud project to `soccer-tracker-fa049`
5. **Run UI Tests**: Executes 9 instrumentation tests on Firebase Test Lab
   - Device: MediumPhone.arm (virtual device, free tier)
   - Android Version: 30 (Android 11)
   - Locale: en, Orientation: portrait
   - Timeout: 10 minutes
   - Test orchestrator enabled
   - Video recording and performance metrics disabled for cost optimization

#### Job 3: create-release (runs only on push to main/master, after all tests pass)

**Depends on**: `firebase-test-lab` job must pass first

**Trigger**: Only executes on push events to `main` or `master` branches (not on PRs)

**Permissions**: Requires `contents: write` permission to create releases and tags

**Steps**:

1. **Download APK**: Retrieves debug APK from previous job artifacts
2. **Generate Tag**: Creates unique release tag using format `v1.0-build-{run_number}`
   - Example: `v1.0-build-42` for the 42nd GitHub Actions run
3. **Create Release**: Uses `softprops/action-gh-release@v1` to publish GitHub release

**Release Contents**:

- **APK Attachment**: `app-debug.apk` ready for installation
- **Release Notes**: Auto-generated with:
  - Build number and commit SHA
  - Branch name
  - Commit message
  - Test results summary (86 unit tests + 9 UI tests)
  - Installation instructions
- **Tag**: Unique version tag for each successful build
- **Status**: Published as a full release (not draft or prerelease)

**Accessing Releases**:

- Navigate to repository → "Releases" section (right sidebar)
- Each successful push to main/master creates a new release
- Download APK directly from release page
- View complete build and test information

**Benefits**:

- ✅ Automatic build verification on every push/PR
- ✅ Unit test suite execution ensures code quality (86 tests)
- ✅ UI test suite validates user interactions (9 tests)
- ✅ Lint checks catch potential issues early
- ✅ Virtual device testing via Firebase Test Lab (free tier)
- ✅ Automatic GitHub releases with APK after all tests pass
- ✅ APKs and test results available for download
- ✅ Consistent build environment (Ubuntu latest, JDK 17)
- ✅ No billing required - uses Firebase's default storage

**Security**:

All sensitive data stored as GitHub secrets:

- `GOOGLE_CLOUD_CREDENTIALS`: Service account JSON key (Firebase Test Lab Admin + Storage Admin roles)
- `GOOGLE_SERVICES_JSON`: Firebase configuration file
- `DEBUG_KEYSTORE`: Base64-encoded debug signing key

### Firebase Test Lab Setup

**Requirements for CI Integration**:

1. **Google Cloud Project**: Enable Cloud Testing API and Cloud Tool Results API in Firebase project
2. **Service Account**: Create with Firebase Test Lab Admin and Storage Admin roles
3. **GitHub Secrets**:
   - `GOOGLE_CLOUD_CREDENTIALS`: JSON key from service account
   - `GOOGLE_SERVICES_JSON`: Firebase configuration file
   - `DEBUG_KEYSTORE`: Base64-encoded debug signing key
4. **No billing required**: Uses Firebase's default storage for test results

**Setup Documentation**: See [FIREBASE_TEST_LAB_SETUP.md](FIREBASE_TEST_LAB_SETUP.md) for complete setup instructions

**Test Configuration**:

- **Device**: MediumPhone.arm (virtual device, free tier)
- **Android Version**: 30 (Android 11)
- **Locale**: en, Orientation: portrait
- **Test Location**: `app/src/androidTest/java/anaware/soccer/tracker/`
- **Test Count**: 9 UI tests covering navigation, input controls, and screen interactions
- **Timeout**: 10 minutes per test run
- **Cost Optimization**: Test orchestrator enabled, video recording and performance metrics disabled

**UI Tests** (`SoccerTrackerAppTest.kt`):

1. `app_launches_successfully` - Verifies bottom navigation appears
2. `navigation_switches_between_tabs` - Tests navigation between all 4 tabs
3. `add_screen_shows_all_required_fields` - Checks Add screen UI elements
4. `action_count_increment_decrement_works` - Tests counter buttons
5. `action_type_selection_works` - Tests action type selection
6. `session_type_toggle_works` - Tests Match/Training toggle
7. `history_screen_shows_empty_state_or_entries` - Verifies History screen
8. `progress_screen_requires_action_type_selection` - Checks Progress screen UI

**Local Testing**:

```bash
# Run UI tests on connected device/emulator
./gradlew connectedAndroidTest

# Run on Firebase Test Lab (requires gcloud CLI setup)
./gradlew assembleDebug assembleDebugAndroidTest
gcloud firebase test android run \
  --app app/build/outputs/apk/debug/app-debug.apk \
  --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device model=MediumPhone.arm,version=30
```

## Lessons Learned

### Technical Insights

1. **Firestore Deserialization**: Requires no-arg constructors (all parameters need defaults)
2. **Boolean Field Naming**: Firestore stores without "is" prefix (`match` not `isMatch`)
3. **ExposedDropdownMenuBox**: Separate filtering logic from component state
4. **StateFlow Optimization**: Use `remember` with dependencies in Compose
5. **Context Passing**: Pass from Composable to ViewModel, don't hold in ViewModel
6. **Code Cleanup**: Remove unused dependencies and code regularly to reduce APK size

### Development Process

1. **Cloud-First Architecture**: Simpler than local-first with manual sync
2. **Optimistic Updates**: Provide instant feedback while cloud operations complete
3. **Material 3 Components**: Rich UI components reduce custom code
4. **Incremental Testing**: Test after each feature addition
5. **Clear Error Messages**: Help users understand what went wrong

### AI Collaboration

**What Worked Well**:
- Clear, specific feature requests
- Iterative refinement based on feedback
- Full error messages for debugging
- Building on established patterns

**Effective Prompting**:
- ✅ "Add opponent tracking with autocomplete"
- ✅ "Store data directly in Firebase Firestore"
- ✅ "Add filters to history screen to search data quickly"
- ❌ "Make the app better" (too vague)
- ❌ "Fix the bug" (need specifics)

## Future Enhancements

### Potential Next Steps

**High Priority**:

1. **Offline Mode**: Add local caching for offline support
2. **Data Export**: CSV export for coach sharing
3. **Multi-Player**: Track multiple children
4. **Real-Time Sync**: Use Firestore real-time listeners

**Medium Priority**:
5. **Photo Attachments**: Add photos to entries
6. **Share Charts**: Export chart as image
7. **Statistics Dashboard**: More advanced analytics
8. **Goals & Milestones**: Set and track progress

**Low Priority**:
9. **Dark Mode Toggle**: Currently follows system
10. **Push Notifications**: Reminders to log sessions
11. **Team Integration**: Share with coach/team
12. **Gamification**: Badges and achievements

## Important Files

### Entry Points
- [MainActivity.kt](app/src/main/java/anaware/soccer/tracker/MainActivity.kt) - Android entry point
- [SoccerTrackerApp.kt](app/src/main/java/anaware/soccer/tracker/ui/SoccerTrackerApp.kt) - Navigation root

### Core Logic
- [SoccerViewModel.kt](app/src/main/java/anaware/soccer/tracker/ui/SoccerViewModel.kt) - Business logic with Firebase integration
- [FirebaseService.kt](app/src/main/java/anaware/soccer/tracker/backup/FirebaseService.kt) - Firebase CRUD operations

### Data Models
- [SoccerAction.kt](app/src/main/java/anaware/soccer/tracker/data/SoccerAction.kt) - Main entity
- [ActionType.kt](app/src/main/java/anaware/soccer/tracker/data/ActionType.kt) - Enum for types
- [BackupData.kt](app/src/main/java/anaware/soccer/tracker/data/BackupData.kt) - Firebase serialization models

### UI Screens
- [AddActionScreen.kt](app/src/main/java/anaware/soccer/tracker/ui/AddActionScreen.kt) - Entry form with date/time picker
- [HistoryScreen.kt](app/src/main/java/anaware/soccer/tracker/ui/HistoryScreen.kt) - List view with advanced filters
- [ChartScreen.kt](app/src/main/java/anaware/soccer/tracker/ui/ChartScreen.kt) - Progress chart with triple filtering
- [BackupScreen.kt](app/src/main/java/anaware/soccer/tracker/ui/BackupScreen.kt) - Account and sync status

### Configuration
- [build.gradle.kts](app/build.gradle.kts) - Dependencies
- [AndroidManifest.xml](app/src/main/AndroidManifest.xml) - Permissions
- [google-services.json](app/google-services.json) - Firebase configuration

## References

### Official Documentation
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
- [Material Design 3](https://m3.material.io/)

### Third-Party Libraries
- [Vico Charts](https://github.com/patrykandpatrick/vico)
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)

### Community Resources
- [Stack Overflow: Android](https://stackoverflow.com/questions/tagged/android)
- [Reddit: r/androiddev](https://reddit.com/r/androiddev)
- [Firebase Community](https://firebase.google.com/community)

## Contact & Contributions

**Project Repository**: https://github.com/anaselhajjaji/soccer-tracking-app-android

**AI Development Assistant**: Claude Sonnet 4.5 (Anthropic)

**Development Date**: December 2025

**Package Name**: `anaware.soccer.tracker`

**Status**: v1.0 - Initial Release

---

*This document serves as a comprehensive reference for understanding the development process, technical decisions, and AI-assisted development workflow for the Soccer Tracker application.*
