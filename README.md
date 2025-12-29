# Soccer Tracker - Offensive Actions Tracker

An Android app for tracking your son's offensive actions during soccer matches and training sessions. All data is stored directly in Firebase Firestore and automatically synced across devices.

## Features

### Core Tracking

- **Record Actions**: Log offensive actions with action type specification:
  - Goals
  - Assists
  - General Offensive Actions
- **Multi-Player Support**: Track multiple players with detailed profiles
  - Player name, birthdate, and jersey number
  - Assign player to each action entry
  - Legacy entries automatically migrated to default "Player"
- **Team Management**: Organize players into teams
  - Team name, color, league, and season
  - Players can belong to multiple teams
  - Assign team when recording actions
- **Match Management**: Organize actions into matches
  - Automatic match creation when recording match actions
  - Manual match management via dedicated UI
  - View, add, edit, and delete matches
  - Track match metadata: date, teams, league, scores
  - Match result calculation (Win/Loss/Draw)
- **Session Types**: Distinguish between match and training sessions
- **Opponent Tracking**: Record opponent name with autocomplete suggestions
- **Optional Date & Time**: Choose current time or select custom date/time
  - "Use current date & time" checkbox (default)
  - Custom selection available when needed
- **Increment Controls**: Easy +/- buttons for quick data entry (minimum 1 action required)
- **Edit Entries**: Modify any recorded action from history screen

### Data Visualization

- **History View**: See all recorded entries with full details including:
  - Date and time
  - Action count with type badges
  - Session type (Match/Training)
  - Opponent name (if specified)
  - Player and team information
  - Edit and delete buttons for each entry
  - **Advanced Filters**: Quickly find specific entries
    - Filter by action type: All, Goals, Assists, or Offensive Actions
    - Filter by session type: Both, Match, or Training
    - Filter by opponent: All, No Opponent, or specific opponent
    - Filter by player: All, Legacy Entries, or specific player
    - Filter by team: All or specific team (with color indicators)
    - Combine filters for precise searches (e.g., "Goals by Player #10 vs Team A in Matches")
    - Visual filter button highlights when filters are active
    - One-tap "Clear All" to reset filters
- **Advanced Progress Chart**: Interactive chart with advanced filtering
  - Select action type: Goals, Assists, or Offensive Actions (required)
  - Filter by session type: Both, Match, or Training
  - Filter by opponent: All or specific opponent
  - Filter by player: All or specific player
  - Filter by team: All or specific team
  - Combine filters for specific insights (e.g., "Goals by Player #10 vs Team A in Matches")
  - Statistics card showing total actions, session count, and averages

### Data Management

- **Edit Entries**: Update any field of recorded actions from history screen
- **Individual Deletion**: Delete specific entries if mistakes are made
- **Player Management**: Add, edit, and delete player profiles
  - Access via floating menu on Account screen
  - Track name, birthdate, jersey number
  - Manage team assignments
- **Team Management**: Add, edit, and delete teams
  - Access via floating menu on Account screen
  - Custom team colors with color picker
  - Track league and season information
- **Match Management**: Add, edit, and delete matches
  - Access via floating menu on Account screen
  - Automatic match creation when adding match actions
  - Track match date, teams, league/tournament, scores
  - View all actions linked to each match
  - Color-coded result badges (Win/Loss/Draw)
- **Firebase Cloud Storage**: Direct cloud storage with automatic sync
  - Firebase Authentication with Google Sign-In (required)
  - All data stored directly in Firebase Firestore
  - Automatic sync across all devices
  - Data automatically scoped per authenticated user
  - Each user's data is private and isolated
  - Real-time data loading on app startup
  - Automatic migration of legacy data

### Technical Features

- **Cloud-First Storage**: All data stored directly in Firebase Firestore
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Responsive Design**: Soccer-themed color scheme with smooth animations
- **Automatic Sync**: Seamless data synchronization across devices
- **Instant Updates**: Immediate UI feedback with optimistic updates

### UI & Navigation

**Navigation Structure:**

- **Hamburger Menu (Drawer)**: Main navigation with organized sections
  - Main: Progress Chart, History, Account
  - Management: Manage Players, Manage Teams, Manage Matches
- **Floating Action Button**: Quick access to Add Entry from any screen
- **Top Bar**: Hamburger icon and dynamic screen titles

**Screen Flow:**

- **Starting Screen**: Progress Chart (default)
- **Add Entry**: Accessed via FAB, full-screen form with all input fields
- **Navigation**: Swipe or click hamburger icon to access drawer menu
- **Management**: Organized section in drawer for data management

## Tech Stack

- **Language**: Kotlin 2.1.0
- **UI Framework**: Jetpack Compose (BOM 2025.01.00)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Firebase Firestore (Cloud NoSQL)
- **Charts**: Vico Chart Library
- **Navigation**: Jetpack Navigation Compose
- **Authentication**: Firebase Authentication with Google Sign-In
- **Serialization**: Kotlinx Serialization
- **Build Tools**: AGP 8.9.1, Gradle 8.12
- **Quality Tools**: Detekt (static analysis), JaCoCo (coverage), Android Lint
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## Project Structure

```
app/src/main/java/anaware/soccer/tracker/
├── backup/
│   └── FirebaseService.kt              # Firebase Firestore CRUD operations
├── data/
│   ├── ActionType.kt                   # Enum for action types
│   ├── BackupData.kt                   # Firestore data models
│   ├── Match.kt                        # Match entity with scores and result
│   ├── Player.kt                       # Player entity with multi-team support
│   ├── SoccerAction.kt                 # Data model for soccer actions
│   └── Team.kt                         # Team entity with color and league
├── ui/
│   ├── AddActionScreen.kt              # Screen for adding new entries
│   ├── BackupScreen.kt                 # Account and sync status UI
│   ├── ChartScreen.kt                  # Progress chart with filtering
│   ├── HistoryScreen.kt                # Screen showing all entries
│   ├── MatchManagementScreen.kt        # Match CRUD interface
│   ├── PlayerManagementScreen.kt       # Player CRUD interface
│   ├── SoccerTrackerApp.kt             # Main app navigation
│   ├── SoccerViewModel.kt              # ViewModel with Firebase integration
│   ├── TeamManagementScreen.kt         # Team CRUD interface
│   └── theme/                          # Material 3 theme files
├── MainActivity.kt                     # Main activity
└── SoccerTrackerApp.kt                 # Application class
```

## Building and Running

### Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 17 or later
- Android SDK with API level 35
- Gradle 8.12
- Firebase project with `google-services.json` file (see [FIREBASE_SETUP.md](FIREBASE_SETUP.md))

### Steps to Build

1. **Open the project in Android Studio**:

   ```bash
   cd soccer-tracking-app-android
   # Open this directory in Android Studio
   ```

2. **Sync Gradle**:
   - Android Studio should automatically prompt you to sync Gradle files
   - If not, click "File" → "Sync Project with Gradle Files"

3. **Build the project**:
   - Click "Build" → "Make Project" or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

4. **Run the app**:
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift+F10`

### Using Gradle from Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Continuous Integration

This project uses GitHub Actions for automated build, test, and quality checks.

### CI Pipeline

The CI workflow automatically runs on:

- Push to `main` or `master` branches
- Pull requests to `main` or `master` branches

**Workflow jobs:**

#### Job 1: Build and Test

1. **Build**: Compiles debug APK with `./gradlew assembleDebug`
2. **Unit Tests**: Runs all unit tests with `./gradlew test` (210 tests across 6 test files)
3. **Lint**: Performs Android code quality checks with `./gradlew lintDebug`
4. **Coverage**: Generates JaCoCo code coverage report with `./gradlew jacocoTestReport`
5. **Detekt**: Runs Kotlin static analysis with `./gradlew detekt`
6. **Build Test APK**: Compiles instrumentation test APK with `./gradlew assembleDebugAndroidTest`
7. **Artifacts**: Uploads APKs and quality reports as artifacts (7-day retention):
   - Debug APK and test APK
   - Lint report (HTML)
   - Coverage report (HTML + XML)
   - Test results (HTML)
   - Detekt report (HTML)

#### Job 2: Firebase Test Lab (Push to main/master only)

1. **Download APKs**: Retrieves debug and test APKs from previous job
2. **Authenticate**: Connects to Google Cloud using service account
3. **Run UI Tests**: Executes instrumentation tests on Firebase Test Lab virtual devices

#### Job 3: Create Release (Push to main/master only, after tests pass)

1. **Download APK**: Retrieves debug APK from previous job
2. **Generate Tag**: Creates unique release tag (e.g., `v1.0-build-42`)
3. **Create Release**: Publishes GitHub release with APK attachment and release notes

**Release includes**:

- Debug APK ready for installation
- Build number and commit information
- Test results summary
- Installation instructions

**Access releases**: Go to repository → "Releases" section on the right sidebar

### Firebase Test Lab Setup

UI tests run automatically on Firebase Test Lab when pushing to `main` or `master` branches.

**Setup required:**

- Google Cloud service account with Firebase Test Lab Admin and Storage Admin roles
- GitHub secrets: `GOOGLE_CLOUD_CREDENTIALS`, `GOOGLE_SERVICES_JSON`, `DEBUG_KEYSTORE`
- **No billing required** - uses Firebase's default storage
- See [FIREBASE_TEST_LAB_SETUP.md](FIREBASE_TEST_LAB_SETUP.md) for detailed setup instructions

**Test configuration:**

- **Device:** MediumPhone.arm (virtual), Android 11 (API 30)
- **Tests:** 30 UI tests covering navigation, input controls, validation, filters, management screens, and screen interactions
- **Location:** `app/src/androidTest/java/anaware/soccer/tracker/`
- **Test Coverage:**
  - 9 navigation and basic UI tests
  - 4 filter button and interaction tests
  - 6 management menu navigation tests
  - 4 match/team section display tests
  - 4 progress chart enhancement tests
  - 5 validation and UI polish tests

### Running Tests Locally

```bash
# Run unit tests
./gradlew test

# Run UI tests on connected device/emulator
./gradlew connectedAndroidTest

# Run UI tests on Firebase Test Lab (requires gcloud CLI)
./gradlew assembleDebug assembleDebugAndroidTest
gcloud firebase test android run \
  --app app/build/outputs/apk/debug/app-debug.apk \
  --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
```

### Quality Checks

Run all quality checks locally:

```bash
# Quick script to run all quality checks and open reports
./quality-check.sh

# Or run individual checks
./gradlew test                # Unit tests (210 tests = 105 × 2 variants)
./gradlew jacocoTestReport   # Code coverage report
./gradlew lintDebug          # Android Lint analysis
./gradlew detekt             # Kotlin static analysis
```

**Quality reports generated:**

- **Test Results**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Coverage Report**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- **Lint Report**: `app/build/reports/lint-results-debug.html`
- **Detekt Report**: `app/build/reports/detekt/detekt.html`

See [QUALITY_REPORTS.md](QUALITY_REPORTS.md) for detailed information about each report.

### Viewing Results

- Check the "Actions" tab in GitHub to see workflow runs
- Download artifacts (APKs, test results, quality reports) from completed workflow runs
- View detailed test reports in Firebase Console → Test Lab
- Build status badge: Add to README if desired

## Using the App

### Adding an Entry

1. Open the app to the "Add" screen
2. Use the + and - buttons to set the number of offensive actions (at least 1 required)
3. Select custom date and time (or use current time)
4. Select the action type: Goal, Assist, or Offensive Action
5. Select whether it was a Match or Training session
6. Optionally enter opponent name (autocomplete will suggest previously entered opponents)
7. Select a player (required)
8. Select a team (required - only shown after player is selected)
9. Tap "Save Entry"

### Viewing History

1. Tap the "History" tab at the bottom
2. See all recorded entries with:
   - Date and time stamps
   - Action count with type (e.g., "3 Goals")
   - Color-coded badges for session type and action type
   - Opponent name (e.g., "vs Team A")
3. **Use Filters** to quickly find specific entries:
   - Tap the filter icon (🎯) in the header
   - Select filters by Action Type, Session Type, and/or Opponent
   - Filter button highlights when filters are active
   - Tap "Clear All" to reset all filters
   - Entry count updates to show filtered results
4. Tap the delete icon on any entry to remove it

### Viewing Progress

1. Tap the "Progress" tab at the bottom
2. **Select an action type** (required): Goal, Assist, or Offensive Action
3. Optionally apply additional filters:
   - **Session Type Filter**: Both, Match, or Training
   - **Opponent Filter**: All opponents, or select a specific opponent
4. View the line chart showing action counts over time for the selected filters
5. See statistics: Total Actions, Number of Sessions, and Average per session
6. Each point on the chart represents one session
7. Use "Show More" to see additional opponents if you've faced many teams

### Account & Sync

#### First Time Setup

1. Open the app - it will attempt to sign in automatically
2. If not signed in, tap the "Account" tab at the bottom
3. Tap "Sign in with Google"
4. Select your Google account
5. Your data will be loaded automatically from Firebase

#### How It Works

- **Automatic Sign-In**: The app signs in automatically on startup using your cached Google credentials
- **Automatic Data Loading**: All your data loads from Firebase when you open the app
- **Instant Saves**: Every new entry is immediately saved to Firebase
- **Cross-Device Sync**: Access your data from any device by signing in with the same Google account
- **No Manual Backup**: Everything is saved automatically - no backup button needed

#### Changing Accounts

1. Go to the "Account" tab
2. Tap "Sign Out"
3. Sign in with a different Google account
4. Your new account's data will load automatically

## Data Storage

### Firebase Firestore Structure

Data is stored in Firebase Firestore with the following structure:

```text
/users
  /{userId}
    /actions
      /{timestamp1}
        - dateTime: "2025-12-19T10:00:00"
        - actionCount: 5
        - actionType: "GOAL"
        - match: true
        - opponent: "Team A"
      /{timestamp2}
        - dateTime: "2025-12-19T14:30:00"
        - actionCount: 2
        - actionType: "ASSIST"
        - match: false
        - opponent: ""
```

### Data Models

**SoccerAction:**

| Field | Type | Description |
| ----- | ---- | ----------- |
| id | Long | Timestamp-based unique ID |
| dateTime | String | ISO-8601 formatted date/time |
| actionCount | Int | Number of offensive actions |
| actionType | String | GOAL, ASSIST, or OFFENSIVE_ACTION |
| isMatch | Boolean | true = match, false = training |
| opponent | String | Name of opponent team (optional) |
| playerId | String | ID of player who performed action |
| teamId | String | ID of team player was representing |
| matchId | String | ID of match this action belongs to |

**Match:**

| Field | Type | Description |
| ----- | ---- | ----------- |
| id | String | UUID-based unique ID |
| date | String | ISO date format (yyyy-MM-dd) |
| playerTeamId | String | ID of player's team |
| opponentTeamId | String | ID of opponent team |
| league | String | League/tournament name (optional) |
| playerScore | Int | Player team's score (-1 = not recorded) |
| opponentScore | Int | Opponent team's score (-1 = not recorded) |
| isHomeMatch | Boolean | True if home match, false if away (default: true) |

## Customization

### Changing Colors

Edit [app/src/main/java/anaware/soccer/tracker/ui/theme/Color.kt](app/src/main/java/anaware/soccer/tracker/ui/theme/Color.kt) to customize the app's color scheme.

### Modifying Data Schema

If you need to add new fields:

1. Update [SoccerAction.kt](app/src/main/java/anaware/soccer/tracker/data/SoccerAction.kt)
2. Update [BackupData.kt](app/src/main/java/anaware/soccer/tracker/data/BackupData.kt) to match new fields
3. Consider backward compatibility with existing Firestore data
4. Update FirebaseService CRUD operations if needed

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture with cloud-first storage:

- **Model**: Data entities and Firebase models ([data/](app/src/main/java/anaware/soccer/tracker/data/))
- **View**: Jetpack Compose screens ([ui/](app/src/main/java/anaware/soccer/tracker/ui/))
- **ViewModel**: [SoccerViewModel.kt](app/src/main/java/anaware/soccer/tracker/ui/SoccerViewModel.kt) manages UI state and business logic
- **Services**: [FirebaseService.kt](app/src/main/java/anaware/soccer/tracker/backup/FirebaseService.kt) handles all Firestore operations

Data flows:

1. User interacts with Compose UI
2. UI calls ViewModel methods with Context
3. ViewModel calls FirebaseService for CRUD operations
4. FirebaseService writes/reads directly to/from Firestore
5. ViewModel updates local StateFlow for immediate UI updates
6. StateFlow automatically updates UI when data changes

### Key Design Patterns

- **Cloud-First Storage**: All data stored directly in Firebase Firestore
- **Optimistic Updates**: Local state updated immediately, then synced to cloud
- **Reactive Streams**: Kotlin Flow for automatic UI updates
- **State Management**: MutableStateFlow for in-memory data management
- **Single Activity**: Navigation Compose for screen management

## Security Considerations

Per organizational coding standards:

- **Input Validation**: All user inputs are validated before database insertion
- **Firebase Authentication**: Secure Google authentication using Firebase Auth
- **Least Privilege**: App only requests necessary Android permissions (Internet, Network State)
- **No Credential Storage**: Firebase handles all authentication tokens
- **Secure Transport**: All Firebase API calls use HTTPS
- **User-Scoped Data**: Each user's data is isolated in Firestore by their Firebase UID

## Firebase Integration Details

### Permissions Required

- `android.permission.INTERNET` - Required for Firebase services
- `android.permission.ACCESS_NETWORK_STATE` - Check connectivity

### Firebase Services Used

- **Firebase Authentication** - Google Sign-In provider
- **Cloud Firestore** - NoSQL document database for all app data

### Data Privacy

- Data is stored in the user's Firestore collection scoped by user ID
- Only the signed-in user can access their data
- Firestore security rules enforce user isolation
- Data structure: `/users/{userId}/actions/{actionId}`
- No data is accessible by other users or third parties
- All data operations require authentication

## Troubleshooting

### Build Issues

**Problem**: `File google-services.json is missing`

**Solution**: Download `google-services.json` from Firebase Console and place it in the `app/` directory. See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for detailed instructions.

**Problem**: Build fails after adding Firebase

**Solution**: Ensure you've added the Google Services plugin to both root and app `build.gradle.kts` files

### Runtime Issues

**Problem**: Google Sign-In fails

**Solution**: Ensure device has Google Play Services installed and updated. Verify SHA-1 certificate is added to Firebase project.

**Problem**: Data not syncing

**Solution**: Check internet connectivity, ensure user is signed in, and verify Firestore is enabled in Firebase Console

**Problem**: "Permission denied" errors

**Solution**: Check Firestore security rules are configured correctly to allow user access to their own data

**Problem**: Chart not showing data

**Solution**: Ensure you've added at least one entry and selected appropriate filters

**Problem**: Data not loading on startup

**Solution**: Check that auto sign-in is working. Go to Account tab to verify sign-in status.

## Future Enhancements

Potential future features:

- Match Details screen with all associated actions
- Match filtering in History and Progress screens
- Match statistics and analytics
- Offline mode with local caching
- Export data to CSV/Excel
- Real-time sync with Firestore listeners
- Statistical insights and trend analysis
- Share progress charts as images
- Set goals and track progress towards them
- Notification reminders to log sessions
- Dark mode toggle
- Head-to-head statistics per opponent
- Photo attachments for entries

## License

This is a personal project for tracking soccer performance data.

## Support

For issues or questions:

- Check code comments for implementation details
- Refer to Android documentation for platform features
- See [CLAUDE.md](CLAUDE.md) for development notes and AI assistance context

## Version

**v1.3.0** - Navigation Improvements with Hamburger Menu (December 2025)

### Latest Changes

**Navigation Restructure:**

The app now features an improved navigation structure with hamburger menu and floating action button:

- **Hamburger Menu (Drawer)**: Replace bottom navigation with side drawer menu
  - **Main Navigation**: Progress Chart, History, Account
  - **Management Section**: Manage Players, Manage Teams, Manage Matches
  - **Organized Layout**: Logical grouping of screens with clear sections
- **Floating Action Button (FAB)**: Always-visible FAB for quick Add Entry access
  - **Primary Action**: Most frequent use case accessible from any screen
  - **Single Tap**: Navigate directly to Add Entry screen
- **Top Bar with Menu Icon**: Hamburger icon to open/close navigation drawer
- **Starting Screen Changed**: Progress Chart is now the default starting screen (instead of Add)
- **Clean Interface**: More screen space for content, less UI clutter

**Benefits:**

- **Better UX**: Primary action (Add Entry) always accessible via FAB
- **More Space**: Drawer navigation frees up bottom of screen for content
- **Organized**: Management functions grouped together in dedicated section
- **Consistent**: Standard Material Design 3 navigation pattern
- **Scalable**: Easy to add more screens/features in the future

**Technical Implementation:**

- **ModalNavigationDrawer**: Material 3 drawer component with custom content
- **DrawerContent**: Composable with header, main menu items, and management section
- **NavigationDrawerItem**: Standard drawer items with icons and selection state
- **FAB Navigation**: FloatingActionButton with launchSingleTop navigation
- **TopAppBar**: Dynamic titles based on current route
- **All 210 unit tests passing** (no changes needed)
- **All 30 UI tests updated and passing** for new navigation structure

---

**v1.2.0** - Match Entity with Automatic Match Creation (December 2025)

**Match Entity with Automatic Match Creation:**

The app now organizes actions into matches with automatic creation and complete management UI:

- **Automatic Match Creation**: When recording a match action, the app automatically creates or finds the appropriate match
- **Match Management UI**: Dedicated screen to view, add, edit, and delete matches
- **Match Cards**: Display team names, date, scores, result chips (Win/Loss/Draw), league, and action count
- **Match Grouping**: Actions are linked to matches, making it easy to see all actions from a specific game
- **Match Metadata**: Each match stores date, player team, opponent team, league/tournament, final score, and home/away status
- **Match Name**: Automatically generated display name (e.g., "Team Blue vs Team Red")
- **Home/Away Field**: Specify whether each match is home or away with FilterChip toggle
- **Unified Team Entity**: Opponent teams are now full Team entities (same as player teams)
- **Smart Matching**: Same date + same teams = same match (prevents duplicates)
- **Score Tracking**: Optional match scores (playerScore and opponentScore) with win/loss/draw calculation
- **Legacy Migration**: Existing opponent strings automatically converted to Team entities and linked to matches
- **Easy Access**: "Manage Matches" option in Account screen's floating action menu

**Data Model Updates:**

- Added Match entity with date, teams, league, scores, and home/away status (8 fields)
- SoccerAction now includes matchId field linking to matches
- BackupData updated to version 4 with match serialization
- Firestore schema now includes `/users/{userId}/matches/` collection
- Match.isHomeMatch field (Boolean, default: true) for home/away tracking

**Technical Implementation:**

- FirebaseService extended with Match CRUD operations
- findOrCreateMatch() helper prevents duplicate match creation
- findOrCreateOpponentTeam() converts opponent strings to Team entities
- Automatic legacy migration on app startup (idempotent and seamless)
- MatchManagementScreen with complete CRUD interface
- ViewModel methods: addMatch(), updateMatch(), deleteMatch(), getMatchById(), getActionsForMatch()
- All 210 unit tests passing (105 tests × 2 variants)
- All 30 UI tests passing on Firebase Test Lab
- Full backward compatibility with existing data

**UI Improvements:**

- **AddActionScreen**: Default player selection, match selection UI, inline match creation, home/away toggle
- **HistoryScreen**: Match names displayed instead of separate team/opponent fields
- **MatchManagementScreen**: Home/away toggle in add/edit match dialog
- **ChartScreen**: Improved filtering with collapsible panel and team-based opponent selection
- **Cleaner Display**: More concise action cards with match grouping information

**Progress Chart Filter Improvements:**

- **Collapsible Filter Panel**: All filters hidden behind toggle button (matches History screen pattern)
- **Filter Button Highlight**: Button changes color when filters are active
- **Opponent Team Dropdown**: Replaced opponent string chips with proper Team entity dropdown
- **Opponent Teams from Matches**: Sources opponent teams from matches collection (consistent data model)
- **Player Filter Cleanup**: Removed "Legacy" option from player filter chips
- **Team Dropdown Selector**: Replaced team filter chips with dropdown for cleaner, more scalable UI
- **"All" Chip Plus Dropdown**: Each filter section has "All" chip + dropdown selector pattern
- **Clear All Filters**: Single button to reset all filters at once
- **Consistent UX**: Matches the collapsible filter pattern used in History screen

**Future Enhancements** (planned but not yet implemented):

- Match Details screen showing all actions in a match
- Match filtering in History and Progress screens
- Match statistics and analytics
- Home/Away badge display in match cards

---

**v1.1.0** - Multi-Player & Team Management (December 2025)

**Multi-Player & Team Management:**

- Track multiple players with name, birthdate, and jersey number
- Manage teams with name, color, league, and season
- Players can belong to multiple teams
- Assign player AND team when adding actions
- Player Management screen with add/edit/delete functionality
- Team Management screen with color picker
- Automatic migration of legacy actions to default "Player"
- Full backward compatibility with existing data
- Firestore schema v3 with player and team collections

**Edit Functionality:**

- Edit button on every history entry card
- Comprehensive edit dialog for updating all fields
- Changes persist to Firebase and update locally
- Optimistic updates for instant UI feedback

**Optional Date/Time:**

- "Use current date & time" checkbox (enabled by default)
- Automatic timestamp when checkbox is enabled
- Custom date/time selection when unchecked
- Simplified data entry for most common use case

**Enhanced Filtering:**

- Filter history by player (specific player or "Legacy Entries")
- Filter history by team with color indicators
- All filters work together (action, session, opponent, player, team)
- Progress charts include player and team filtering

**Management Access:**

- Floating action menu on Account screen
- Direct access to player and team management
- Hamburger menu navigation with drawer + FAB for Add Entry

**Technical:**

- Extended data models with player and team support
- SoccerAction now includes playerId and teamId fields
- BackupData v3 with player and team serialization

---

**v1.0.1** - Test Suite Enhancements & Bug Fixes (December 2025)

**Test Suite Enhancements:**
- Increased unit tests from 55 to 210 tests (+155 tests, 282% increase)
- All 210 tests passing (105 tests × 2 variants: debug and release)
- Increased UI tests from 17 to 30 tests (+13 tests, 76% increase)
- Added comprehensive Match entity tests (29 tests)
- Added BackupData serialization tests with isHomeMatch field
- Added comprehensive ViewModel filtering tests (77% coverage)
- Created UiStateTest with 100% coverage
- Added edge case tests for SoccerAction data model
- Added UI tests for filters, management screens, and v1.2.0 features
- Fixed JaCoCo exclusions to properly filter Compose synthetic classes
- Coverage now accurately reflects testable business logic

**Unit Test Coverage:**
- MatchTest.kt: 29 tests covering all Match entity functionality
- SoccerViewModelTest.kt: Comprehensive filtering and CRUD tests
- BackupDataTest.kt: Serialization tests with isHomeMatch support
- ActionTypeTest.kt: Enum validation tests
- SoccerActionTest.kt: Data model edge case tests
- UiStateTest.kt: 100% coverage of UI state class

**UI Test Coverage (Firebase Test Lab):**
- 9 navigation and basic UI tests
- 4 filter button and interaction tests
- 6 management menu navigation tests
- 4 match/team section display tests
- 3 progress chart enhancement tests
- 4 validation and UI polish tests

**Bug Fixes:**
- Fixed Firebase service test that was attempting real Firebase calls
- Corrected JaCoCo configuration to exclude compiler-generated synthetic classes
- Improved test reliability and accuracy

**Documentation:**
- Updated all documentation with new test counts (210 unit, 32 UI)
- Enhanced CLAUDE.md with detailed test suite breakdown
- Updated README.md with test coverage details
- Updated QUALITY_REPORTS.md with latest metrics

---

**v1.0.0** - Initial Release (December 2025)

**Core Features:**
- Action tracking with specific types (Goals, Assists, Offensive Actions)
- Session type differentiation (Match/Training)
- Opponent tracking with autocomplete
- Custom date/time selection
- Advanced history filters (action type, session type, opponent)
- Progress charts with triple filtering
- Firebase Firestore cloud storage
- Automatic Google Sign-In
- Cross-device synchronization
- Individual entry deletion
