# Soccer Tracker - Offensive Actions Tracker

An Android app for tracking your son's offensive actions during soccer matches and training sessions. All data is stored directly in Firebase Firestore and automatically synced across devices.

## Features

### Core Tracking

- **Record Actions**: Log offensive actions with action type specification:
  - Goals
  - Assists
  - General Offensive Actions
- **Session Types**: Distinguish between match and training sessions
- **Opponent Tracking**: Record opponent name with autocomplete suggestions
- **Custom Date & Time**: Choose when the action occurred (not just current time)
- **Zero Actions Support**: Save entries with 0 actions to track participation
- **Increment Controls**: Easy +/- buttons for quick data entry

### Data Visualization

- **History View**: See all recorded entries with full details including:
  - Date and time
  - Action count with type badges
  - Session type (Match/Training)
  - Opponent name (if specified)
  - **Advanced Filters**: Quickly find specific entries
    - Filter by action type: All, Goals, Assists, or Offensive Actions
    - Filter by session type: Both, Match, or Training
    - Filter by opponent: All, No Opponent, or specific opponent
    - Combine filters for precise searches (e.g., "Goals vs Team A in Matches")
    - Visual filter button highlights when filters are active
    - One-tap "Clear All" to reset filters
- **Advanced Progress Chart**: Interactive chart with triple filtering
  - Select action type: Goals, Assists, or Offensive Actions (required)
  - Filter by session type: Both, Match, or Training
  - Filter by opponent: All or specific opponent
  - Combine filters for specific insights (e.g., "Goals vs Team A in Matches")
  - Statistics card showing total actions, session count, and averages

### Data Management

- **Individual Deletion**: Delete specific entries if mistakes are made
- **Firebase Cloud Storage**: Direct cloud storage with automatic sync
  - Firebase Authentication with Google Sign-In (required)
  - All data stored directly in Firebase Firestore
  - Automatic sync across all devices
  - Data automatically scoped per authenticated user
  - Each user's data is private and isolated
  - Real-time data loading on app startup

### Technical Features

- **Cloud-First Storage**: All data stored directly in Firebase Firestore
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Responsive Design**: Soccer-themed color scheme with smooth animations
- **Automatic Sync**: Seamless data synchronization across devices
- **Instant Updates**: Immediate UI feedback with optimistic updates

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Firebase Firestore (Cloud NoSQL)
- **Charts**: Vico Chart Library
- **Navigation**: Jetpack Navigation Compose
- **Authentication**: Firebase Authentication with Google Sign-In
- **Serialization**: Kotlinx Serialization
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/src/main/java/anaware/soccer/tracker/
├── backup/
│   └── FirebaseService.kt      # Firebase Firestore CRUD operations
├── data/
│   ├── ActionType.kt           # Enum for action types
│   ├── BackupData.kt           # Firestore data models
│   └── SoccerAction.kt         # Data model for soccer actions
├── ui/
│   ├── AddActionScreen.kt      # Screen for adding new entries
│   ├── BackupScreen.kt         # Account and sync status UI
│   ├── ChartScreen.kt          # Progress chart with filtering
│   ├── HistoryScreen.kt        # Screen showing all entries
│   ├── SoccerTrackerApp.kt     # Main app navigation
│   ├── SoccerViewModel.kt      # ViewModel with Firebase integration
│   └── theme/                  # Material 3 theme files
├── MainActivity.kt             # Main activity
└── SoccerTrackerApp.kt         # Application class
```

## Building and Running

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API level 34
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
2. **Unit Tests**: Runs all unit tests with `./gradlew test` (55 tests)
3. **Lint**: Performs code quality checks with `./gradlew lintDebug`
4. **Build Test APK**: Compiles instrumentation test APK with `./gradlew assembleDebugAndroidTest`
5. **Artifacts**: Uploads debug APK, test APK, and lint reports (7-day retention)

#### Job 2: Firebase Test Lab (Push to main/master only)

1. **Download APKs**: Retrieves debug and test APKs from previous job
2. **Authenticate**: Connects to Google Cloud using service account
3. **Run UI Tests**: Executes instrumentation tests on Firebase Test Lab physical devices
4. **Upload Results**: Stores test results with 30-day retention

### Firebase Test Lab Setup

UI tests run automatically on Firebase Test Lab when pushing to `main` or `master` branches.

**Setup required:**

- Google Cloud service account with Firebase Test Lab Admin role
- GitHub secret: `GOOGLE_CLOUD_CREDENTIALS` (JSON service account key)
- **No billing required** - uses Firebase's default storage
- See [FIREBASE_TEST_LAB_SETUP.md](FIREBASE_TEST_LAB_SETUP.md) for detailed setup instructions

**Test configuration:**

- **Device:** Pixel 2, Android 11 (API 30)
- **Tests:** 9 UI tests covering navigation, input controls, and screen interactions
- **Location:** `app/src/androidTest/java/anaware/soccer/tracker/`

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

### Viewing Results

- Check the "Actions" tab in GitHub to see workflow runs
- Download artifacts (APKs, test results, lint reports) from completed workflow runs
- View detailed test reports in Firebase Console → Test Lab
- Build status badge: Add to README if desired

## Using the App

### Adding an Entry

1. Open the app to the "Add" screen
2. Use the + and - buttons to set the number of offensive actions (0 or more)
3. Select custom date and time (or use current time)
4. Select the action type: Goal, Assist, or Offensive Action
5. Select whether it was a Match or Training session
6. Optionally enter opponent name (autocomplete will suggest previously entered opponents)
7. Tap "Save Entry"

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

### SoccerAction Data Model

| Field | Type | Description |
| ----- | ---- | ----------- |
| id | Long | Timestamp-based unique ID |
| dateTime | String | ISO-8601 formatted date/time |
| actionCount | Int | Number of offensive actions |
| actionType | String | GOAL, ASSIST, or OFFENSIVE_ACTION |
| isMatch | Boolean | true = match, false = training |
| opponent | String | Name of opponent team (optional) |

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

- Offline mode with local caching
- Export data to CSV/Excel
- Multiple players support
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

**v1.0** - Initial Release (December 2025)

### Features

- Action tracking with specific types (Goals, Assists, Offensive Actions)
- Session type differentiation (Match/Training)
- Opponent tracking with autocomplete
- Custom date/time selection
- Zero action entries for participation tracking
- Advanced history filters (action type, session type, opponent)
- Progress charts with triple filtering
- Firebase Firestore cloud storage
- Automatic Google Sign-In
- Cross-device synchronization
- Individual entry deletion
