# Firebase Test Lab Setup for CI/CD

This guide explains how to set up Firebase Test Lab integration with GitHub Actions for automated UI testing.

## Prerequisites

- Google Cloud Platform (GCP) account
- Firebase project (same project used for the app)
- GitHub repository with admin access
- **No billing required** - Tests use Firebase's default storage

## Step 1: Enable Firebase Test Lab API

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project
3. Navigate to **APIs & Services** → **Library**
4. Search for "**Cloud Testing API**"
5. Click **Enable**

## Step 2: Create Service Account

1. In Google Cloud Console, go to **IAM & Admin** → **Service Accounts**
2. Click **Create Service Account**
3. Name: `github-actions-test-lab`
4. Description: `Service account for GitHub Actions Firebase Test Lab integration`
5. Click **Create and Continue**

### Assign Roles

Add the following role to the service account:

- **Firebase Test Lab Admin** - Allows running tests

Click **Continue** → **Done**

## Step 3: Create Service Account Key

1. Click on the service account you just created
2. Go to **Keys** tab
3. Click **Add Key** → **Create new key**
4. Select **JSON** format
5. Click **Create** (downloads the JSON key file)

**⚠️ Important:** Keep this file secure. Never commit it to your repository.

## Step 4: Configure GitHub Secrets

Add the following secrets to your GitHub repository:

### Navigate to Secrets

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**

### Add Secret

#### GOOGLE_CLOUD_CREDENTIALS

- **Name:** `GOOGLE_CLOUD_CREDENTIALS`
- **Value:** Contents of the JSON key file from Step 3
  - Open the downloaded JSON file in a text editor
  - Copy the entire contents
  - Paste into the secret value

That's it! Only one secret is needed.

## Step 5: Verify Configuration

### Test Locally (Optional)

Before running in CI, you can test Firebase Test Lab locally:

```bash
# Install Google Cloud SDK
# https://cloud.google.com/sdk/docs/install

# Authenticate
gcloud auth login

# Set project
gcloud config set project YOUR_PROJECT_ID

# Build APKs
./gradlew assembleDebug assembleDebugAndroidTest

# Run test on Firebase Test Lab
gcloud firebase test android run \
  --type instrumentation \
  --app app/build/outputs/apk/debug/app-debug.apk \
  --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
  --device model=Pixel2,version=30,locale=en,orientation=portrait
```

### Push to GitHub

Once configured, push to `main` or `master` branch to trigger the CI/CD pipeline:

```bash
git add .
git commit -m "Add Firebase Test Lab CI integration"
git push origin main
```

## Step 6: Monitor Test Results

### In GitHub Actions

1. Go to your repository → **Actions** tab
2. Click on the latest workflow run
3. View the `firebase-test-lab` job
4. Check the test execution logs for pass/fail status

### In Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Navigate to **Test Lab** in the left sidebar
4. View test history and detailed results

## Workflow Configuration

The GitHub Actions workflow (`.github/workflows/android-build.yml`) includes two jobs:

### Job 1: build-and-test

- Builds APKs
- Runs unit tests
- Runs lint checks
- Uploads artifacts

### Job 2: firebase-test-lab

- **Triggers:** Only on push to `main` or `master` branches (not on PRs)
- **Depends on:** `build-and-test` job must pass first
- Downloads APK artifacts
- Authenticates with Google Cloud
- Runs UI tests on Firebase Test Lab
- Results viewable in GitHub Actions logs and Firebase Console

## Test Configuration

### Default Device

The workflow is configured to run tests on:

- **Model:** Pixel 2
- **Android Version:** 30 (Android 11)
- **Locale:** en (English)
- **Orientation:** portrait

### Customize Devices

To test on multiple devices, modify `.github/workflows/android-build.yml`:

```yaml
- name: Run UI tests on Firebase Test Lab
  run: |
    gcloud firebase test android run \
      --type instrumentation \
      --app app-debug.apk \
      --test app-debug-androidTest.apk \
      --device model=Pixel2,version=30 \
      --device model=Pixel4,version=31 \
      --device model=redfin,version=33 \
      --timeout 15m
```

### Available Devices

List available devices:

```bash
gcloud firebase test android models list
```

## Cost Considerations

Firebase Test Lab pricing:

- **Free tier:** 10 tests/day on physical devices, 5 tests/day on virtual devices
- **Spark plan (free):** Limited daily usage
- **Blaze plan (pay-as-you-go):** Charges after free tier

View pricing: https://firebase.google.com/pricing

### Cost Optimization Tips

1. **Limit test frequency:** The workflow only runs on push to main/master (not on PRs)
2. **Single device testing:** Default config uses one device to minimize costs
3. **Disable features:** `--no-record-video` and `--no-performance-metrics` reduce test time
4. **Set timeouts:** `--timeout 10m` prevents runaway tests

## Troubleshooting

### Authentication Errors

**Error:** `ERROR: (gcloud.firebase.test.android.run) Invalid credentials`

**Solution:**
- Verify `GOOGLE_CLOUD_CREDENTIALS` secret contains valid JSON
- Check service account has correct roles
- Ensure Cloud Testing API is enabled

### APK Not Found

**Error:** `app-debug.apk: No such file or directory`

**Solution:**
- Check artifact upload/download step names match
- Verify APKs are being built successfully in first job
- Check artifact retention hasn't expired

### Test Failures

**Error:** Tests fail on Firebase Test Lab but pass locally

**Solution:**
- Check device compatibility (API level, screen size)
- Verify Firebase authentication works on test devices
- Review test logs in Firebase Console
- Consider adding wait times for UI elements

### Quota Exceeded

**Error:** `Quota exceeded for quota metric 'Test executions'`

**Solution:**
- Wait until daily quota resets (midnight UTC)
- Upgrade to Blaze plan if more tests needed
- Reduce test frequency or device matrix

## Security Best Practices

1. **Never commit service account keys** to your repository
2. **Use GitHub Secrets** for all sensitive credentials
3. **Limit service account permissions** to only what's needed
4. **Rotate keys regularly** (every 90 days recommended)
5. **Monitor usage** in Google Cloud Console billing dashboard

## Additional Resources

- [Firebase Test Lab Documentation](https://firebase.google.com/docs/test-lab)
- [gcloud CLI Reference](https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)

## Support

For issues with:
- **Firebase Test Lab:** Check [Firebase Support](https://firebase.google.com/support)
- **GitHub Actions:** Check [GitHub Community](https://github.community/)
- **App-specific tests:** Review test code in `app/src/androidTest/`
