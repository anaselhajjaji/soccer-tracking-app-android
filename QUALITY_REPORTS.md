# Quality Reports Guide

This document explains all the quality reports generated for the Soccer Tracker app.

## Available Reports

### 1. Detekt Static Analysis Report (Kotlin)

**What it shows:**
- Code smells and anti-patterns
- Complexity metrics
- Style violations
- Potential bugs
- Best practice violations
- 164 issues found (formatting, wildcards, complexity)

**Location (Local):**
```
app/build/reports/detekt/detekt.html
```

**Location (CI):**
- Go to GitHub Actions → Select workflow run
- Download "detekt-report" artifact
- Extract and open `detekt.html`

**Generate locally:**
```bash
./gradlew detekt
open app/build/reports/detekt/detekt.html
```

**Categories analyzed:**
- Complexity (method length, nesting, parameters)
- Empty blocks
- Exceptions handling
- Formatting (spacing, naming)
- Naming conventions
- Performance issues
- Potential bugs
- Style issues

### 2. Test Results Report

**What it shows:**
- All unit test results (55 tests)
- Pass/fail status for each test
- Test execution time
- Test suites breakdown

**Location (Local):**
```
app/build/reports/tests/testDebugUnitTest/index.html
```

**Location (CI):**
- Go to GitHub Actions → Select workflow run
- Download "test-report" artifact
- Extract and open `testDebugUnitTest/index.html`

**Generate locally:**
```bash
./gradlew test
open app/build/reports/tests/testDebugUnitTest/index.html
```

### 3. Code Coverage Report (JaCoCo)

**What it shows:**
- Line coverage percentage
- Branch coverage percentage
- Package/class/method level coverage
- Uncovered lines highlighted in red
- Detailed coverage metrics

**Location (Local):**
```
app/build/reports/jacoco/jacocoTestReport/html/index.html
```

**Location (CI):**
- Go to GitHub Actions → Select workflow run
- Download "coverage-report" artifact
- Extract and open `html/index.html`

**Generate locally:**
```bash
./gradlew test jacocoTestReport
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

**Coverage breakdown:**
- Data classes: Full coverage expected
- ViewModels: Business logic coverage
- Services: Firebase operations (may need mocking for 100%)
- UI Screens: Limited coverage (Compose UI testing needed)

### 4. Android Lint Report

**What it shows:**
- Code quality issues
- Performance problems
- Security vulnerabilities
- Accessibility issues
- Obsolete dependencies
- Icon and resource issues

**Location (Local):**
```
app/build/reports/lint-results-debug.html
```

**Location (CI):**
- Go to GitHub Actions → Select workflow run
- Download "lint-report" artifact
- Open `lint-results-debug.html`

**Generate locally:**
```bash
./gradlew lintDebug
open app/build/reports/lint-results-debug.html
```

**Current status:**
- 8 warnings (all for dependencies requiring SDK 36)
- 0 errors

**Detekt:**
- 164 issues found (formatting, wildcards, complexity)
- Report generated successfully
- Does not fail build (informational only)

### 5. Build Report

**What it shows:**
- Build success/failure
- Compilation warnings
- Dependency resolution
- Task execution times

**Location (CI):**
- GitHub Actions → Workflow run logs
- Expand each step to see output

**View locally:**
```bash
./gradlew assembleDebug --info
```

## Quick Access Commands

### Run All Quality Checks

Use the provided script:
```bash
./quality-check.sh
```

This will:
1. Clean build
2. Run tests
3. Generate coverage report
4. Run lint
5. Run Detekt static analysis
6. Open all reports in browser

### Individual Commands

```bash
# Tests only
./gradlew test

# Coverage only
./gradlew jacocoTestReport

# Lint only
./gradlew lintDebug

# Detekt only
./gradlew detekt

# All quality checks
./gradlew clean test jacocoTestReport lintDebug detekt
```

## CI/CD Artifacts

Every GitHub Actions run generates these artifacts:

| Artifact Name | Contents | Retention |
|--------------|----------|-----------|
| `app-debug` | Debug APK | 7 days |
| `app-debug-androidTest` | Test APK | 7 days |
| `detekt-report` | Detekt HTML | 7 days |
| `lint-report` | Lint HTML report | 7 days |
| `coverage-report` | JaCoCo HTML + XML | 7 days |
| `test-report` | Unit test HTML | 7 days |

### Downloading Artifacts

1. Go to your repository on GitHub
2. Click "Actions" tab
3. Select a workflow run
4. Scroll to "Artifacts" section at bottom
5. Click to download (ZIP file)
6. Extract and open HTML files in browser

## Report Summaries

### Current Quality Metrics

Based on latest build:

**Tests:**
- ✅ 55 unit tests passing
- ✅ 9 UI tests passing
- ⏱️ Test execution: ~5 seconds

**Coverage:**
- Data models: ~90%+ (high coverage)
- ViewModels: ~70% (business logic)
- Overall: ~60-70% (typical for Android apps)

**Lint:**
- ⚠️ 8 warnings (dependency updates)
- ✅ 0 errors
- ✅ 0 security issues

**Build:**
- ✅ Clean build succeeds
- ⚠️ Minor deprecation warnings (Google Sign-In API)

## Understanding Coverage Reports

### What Good Coverage Looks Like

**High priority (aim for 80%+):**
- Data models (BackupAction, SoccerAction)
- Business logic (SoccerViewModel)
- Utility functions
- Database operations (FirebaseService)

**Medium priority (aim for 60%+):**
- UI state management
- Navigation logic
- Data transformations

**Low priority (UI):**
- Composable functions
- UI layouts
- Theme definitions

### How to Improve Coverage

1. **Add ViewModel tests:**
```kotlin
// Example test
@Test
fun `addAction should update state`() = runTest {
    viewModel.addAction(...)
    assertEquals(expected, viewModel.uiState.value)
}
```

2. **Add Firebase service tests:**
```kotlin
// Mock Firebase for testing
@Test
fun `getAllActions returns all actions`() {
    // Test implementation
}
```

3. **Add UI tests** (already have 9):
- Currently in `app/src/androidTest/`
- Run on Firebase Test Lab in CI

## Viewing Reports Locally

### Option 1: Command Line

```bash
# Generate and open all reports
./quality-check.sh
```

### Option 2: Manual

```bash
# Generate reports
./gradlew clean test jacocoTestReport lintDebug

# Open in browser (macOS)
open app/build/reports/tests/testDebugUnitTest/index.html
open app/build/reports/jacoco/jacocoTestReport/html/index.html
open app/build/reports/lint-results-debug.html

# Open in browser (Linux)
xdg-open app/build/reports/tests/testDebugUnitTest/index.html
xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
xdg-open app/build/reports/lint-results-debug.html
```

### Option 3: Android Studio

1. Run tests: Right-click on test folder → "Run Tests"
2. View coverage: Run → "Run with Coverage"
3. View lint: Analyze → "Inspect Code"

## Quality Gates

### What Passes CI

Current requirements (all must pass):
- ✅ Build succeeds
- ✅ All 55 unit tests pass
- ✅ All 9 UI tests pass (Firebase Test Lab)
- ✅ Lint checks pass (no errors)

### Optional Checks

You can add stricter requirements:
- Minimum coverage percentage (e.g., 70%)
- Maximum lint warnings (e.g., 10)
- No critical security issues

## Comparing to SonarCloud

| Feature | Local Reports | SonarCloud |
|---------|---------------|------------|
| **Test Results** | ✅ Yes | ✅ Yes |
| **Coverage** | ✅ Yes (JaCoCo) | ✅ Yes |
| **Lint Issues** | ✅ Yes | ✅ Yes |
| **Security Analysis** | ⚠️ Basic (Lint) | ✅ Advanced |
| **Code Smells** | ⚠️ Limited | ✅ Comprehensive |
| **Complexity** | ❌ No | ✅ Yes |
| **Technical Debt** | ❌ No | ✅ Yes |
| **Historical Trends** | ❌ No | ✅ Yes |
| **Quality Gate** | ❌ Manual | ✅ Automatic |
| **PR Comments** | ❌ No | ✅ Yes |
| **Setup** | ✅ None | ⚠️ Account needed |
| **Cost** | ✅ Free | ✅ Free (open source) |

**Current setup:** Local reports only (no SonarCloud)

## Troubleshooting

### Reports Not Generated

**Problem:** No HTML files after running Gradle

**Solution:**
```bash
# Clean and regenerate
./gradlew clean test jacocoTestReport lintDebug

# Check for errors
./gradlew test --info
```

### Coverage Shows 0%

**Problem:** JaCoCo shows no coverage

**Solution:**
```bash
# Ensure tests run first
./gradlew clean test jacocoTestReport

# Verify .exec files exist
ls -la app/build/jacoco/
```

### Lint Report Empty

**Problem:** Lint report has no issues

**Solution:**
This is good! It means:
- No errors found
- Code follows Android best practices

### Can't Open Reports

**Problem:** Browser doesn't open files

**Solution:**
```bash
# Manually navigate to report
cd app/build/reports/jacoco/jacocoTestReport/html
open index.html

# Or use full path
open /full/path/to/app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Best Practices

### Local Development

1. Run `./quality-check.sh` before committing
2. Fix any test failures immediately
3. Review coverage for new code
4. Address lint warnings

### CI/CD

1. Download artifacts after each run
2. Review coverage trends
3. Address new lint warnings
4. Keep tests passing

### Team Workflow

1. Share reports in PR reviews
2. Set coverage targets as team goals
3. Track lint warnings over time
4. Celebrate improvements!

## Additional Tools

Want more analysis? Consider adding:

1. **Detekt** - Kotlin static analysis
2. **ktlint** - Kotlin code formatter
3. **Dependency updates** - Gradle versions plugin
4. **SonarLint** - IDE integration

See [LOCAL_SONAR_SETUP.md](LOCAL_SONAR_SETUP.md) for details.

## Summary

✅ **What you have now:**
- Complete test results with pass/fail status
- Detailed code coverage with line-by-line analysis
- Android Lint quality and security checks
- All reports available locally and in CI artifacts

🚫 **What you don't need:**
- SonarCloud account
- Cloud services
- External dependencies

🎯 **What you can do:**
- Download reports from any GitHub Actions run
- View detailed HTML reports locally
- Track quality metrics over time manually
- Share reports with team via artifacts

Perfect for personal projects or teams that want full control over their quality analysis!
