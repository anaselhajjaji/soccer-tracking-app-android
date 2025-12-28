# Firebase Setup Instructions

This app uses Firebase Authentication and Firestore for user-scoped data backup and restore.

## Prerequisites

- A Google account
- Android Studio installed
- The Soccer Tracker app project

## Step 1: Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select an existing project
3. Follow the setup wizard:
   - Enter a project name (e.g., "Soccer Tracker")
   - Accept the terms and conditions
   - (Optional) Enable Google Analytics
   - Click "Create project"

## Step 2: Add Android App to Firebase Project

1. In the Firebase Console, click the Android icon to add an Android app
2. Enter the Android package name: `anaware.soccer.tracker`
3. (Optional) Enter an app nickname: "Soccer Tracker"
4. (Optional) Enter SHA-1 certificate:
   - Get your SHA-1 by running: `./gradlew signingReport`
   - Look for the "SHA1" value under "Task :app:signingReport"
   - Or use: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`
5. Click "Register app"

## Step 3: Download google-services.json

1. Download the `google-services.json` file from the Firebase Console
2. Move the file to the `app/` directory of your project:
   ```
   tracking-app-android/
   └── app/
       └── google-services.json  ← Place file here
   ```

**IMPORTANT**: Add this file to `.gitignore` to avoid committing it to version control:
```
# Add to .gitignore
google-services.json
```

## Step 4: Enable Firebase Authentication

1. In the Firebase Console, go to "Authentication" in the left sidebar
2. Click "Get started"
3. Go to the "Sign-in method" tab
4. Enable "Google" as a sign-in provider:
   - Click on "Google"
   - Toggle "Enable"
   - Select a support email
   - Click "Save"

## Step 5: Enable Firestore Database

1. In the Firebase Console, go to "Firestore Database" in the left sidebar
2. Click "Create database"
3. Choose a location (select one close to your users)
4. Start in "Production mode" (more secure)
5. Click "Create"

## Step 6: Configure Firestore Security Rules

1. In Firestore, go to the "Rules" tab
2. Replace the default rules with the following to ensure data is scoped per user:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only read/write their own data
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

3. Click "Publish"

## Step 7: Build and Run

1. Sync Gradle files in Android Studio
2. Build the app: `./gradlew assembleDebug`
3. Run the app on your device or emulator
4. Go to the "Account" tab
5. Sign in with Google
6. All data is automatically saved to Firebase when you add entries

## Data Structure in Firestore

Your data will be stored in Firestore with the following structure:

```
/users/{userId}/actions/{actionId}
  - dateTime: ISO-8601 timestamp string
  - actionCount: Number of actions
  - actionType: "GOAL", "ASSIST", or "OFFENSIVE_ACTION"
  - match: Boolean (true = match, false = training)
  - opponent: String (opponent team name, optional)
  - playerId: String (player ID, required)
  - teamId: String (team ID, required)

/users/{userId}/players/{playerId}
  - id: String (UUID)
  - name: String (player name)
  - birthdate: String (ISO date format)
  - number: Int (jersey number)
  - teams: List<String> (team IDs)

/users/{userId}/teams/{teamId}
  - id: String (UUID)
  - name: String (team name)
  - color: String (hex color code)
  - league: String (optional)
  - season: String (optional)
```

## Troubleshooting

### Build error: "File google-services.json is missing"
- Ensure `google-services.json` is in the `app/` directory
- Verify the file is not corrupted
- Re-download the file from Firebase Console if needed

### Sign-in fails with "Sign-in failed: No ID token received"
- Ensure you added the SHA-1 certificate to your Firebase project
- Verify Google Sign-In is enabled in Firebase Console
- Try generating a new SHA-1 and adding it to Firebase

### "Permission denied" when backing up or restoring
- Check that Firestore security rules are configured correctly
- Verify the user is authenticated before attempting backup/restore
- Check the Firebase Console for any security rule violations

### Data not appearing after restore
- Verify the backup exists in Firestore (check Firebase Console)
- Ensure you're signed in with the same account that created the backup
- Check the app logs for any error messages

## Security Notes

- Never commit `google-services.json` to version control
- Each user's data is isolated by their Firebase UID
- Firestore security rules prevent users from accessing other users' data
- Backups are stored encrypted in Firebase Cloud

## Cost Considerations

- Firebase has a generous free tier (Spark Plan)
- Free tier includes:
  - 50,000 Firestore document reads per day
  - 20,000 Firestore document writes per day
  - 1 GB Firestore storage
  - 10 GB network egress per month
- For personal use with one or few users, the free tier should be more than sufficient

## Support

For more information:
- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
