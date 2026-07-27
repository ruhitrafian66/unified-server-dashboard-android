# Android App Deployment Guide

## Build Issue Resolution

The Gradle build is having issues completing remotely. The Kotlin compilation succeeds, but the build appears to hang during the DEX/APK packaging stages. This is often related to memory constraints or Gradle daemon issues.

## Local Deployment Steps

### Option 1: Build from Android Studio (Recommended)

1. Open the project in Android Studio
2. Go to **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Wait for the build to complete
4. Connect your Android device (RFGYC31M9GD)
5. Go to **Run** → **Run 'app'** or press `Shift + F10`
6. Select your device and click OK

### Option 2: Command Line with Gradle Wrapper

If using the command line locally, try:

```bash
# Clean build
./gradlew clean

# Build APK
./gradlew assembleDebug

# If the above hangs, try with limited workers
./gradlew assembleDebug --max-workers=1 --no-daemon

# Install to device
./gradlew installDebug

# Or manually with adb
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

###Option 3: Direct ADB Installation (After successful build)

Once you have a built APK, you can install it directly:

```bash
# List connected devices
adb devices

# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n com.unifiedserver.dashboard/.MainActivity

# View logs
adb logcat | grep -i "unified\|dashboard"
```

## Troubleshooting Build Issues

### Issue: Build hangs at DEX stage

**Solutions:**
1. Reduce Gradle workers: `./gradlew assembleDebug --max-workers=1`
2. Disable daemon: `./gradlew assembleDebug --no-daemon`
3. Increase Gradle memory in `gradle.properties`:
   ```
   org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=1024m
   ```
4. Clean build files:
   ```bash
   ./gradlew clean
   rm -rf .gradle app/build
   ./gradlew assembleDebug
   ```

### Issue: Build fails with Kotlin compilation error

The Kotlin compilation works fine - the error is likely later in the build process. Try:
1. Check for syntax errors in recent changes
2. Verify all imports are correct
3. Run: `./gradlew compileDebugKotlin` to test just Kotlin compilation

### Issue: APK installation fails

```bash
# Clear app data and reinstall
adb uninstall com.unifiedserver.dashboard
adb install app/build/outputs/apk/debug/app-debug.apk

# Or force install with -r flag
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Device Info

**Connected Device:**
- Serial: RFGYC31M9GD
- Used for testing and deployment

**App Details:**
- Package: `com.unifiedserver.dashboard`
- Activity: `com.unifiedserver.dashboard.MainActivity`
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)

##Testing After Installation

Once installed, the app will:

1. **Show Setup Screen** on first launch
   - Enter your server URL (e.g., `http://192.168.1.100:3001`)
   - Tap "Connect" to test the connection
   - Upon success, automatically navigate to Dashboard

2. **Display Dashboard** with:
   - Server health status
   - Quick stats (total torrents, downloading, seeding, paused)
   - System info (CPU, Memory, Disk, Uptime)
   - Active torrents preview

3. **Navigate** using bottom navigation bar:
   - **Dashboard** - Overview and quick stats
   - **Torrents** - Full torrent list with search
   - **TV Shows** - Show tracking and episode management
   - **System** - Detailed system info, services, disks, Docker containers
   - **Settings** - Configuration and preferences

## Backend Requirements

The app requires a backend server running at the configured URL. The server should provide these endpoints:

- `GET /api/health` - Server health check
- `GET /api/qbittorrent/torrents` - List torrents
- `GET /api/omv/system` - System information
- `GET /api/shows` - List TV shows
- `GET /api/omv/services` - System services
- `GET /api/omv/disks` - Disk information
- And more... (see `ApiService.kt` for full list)

## Additional Resources

- See `BUILD_SUMMARY.md` for implementation details
- Check `.kiro/settings/` for any IDE-specific configuration
- Review `build.gradle.kts` for dependency information

## Quick Verification Checklist

- [x] Kotlin compilation successful
- [x] All imports verified
- [x] Navigation routing configured
- [x] ViewModels with state management
- [x] DI setup with Hilt
- [x] API layer integrated
- [x] Preferences storage configured
- [ ] Build APK (local machine required)
- [ ] Device deployment (local machine required)
- [ ] Integration testing (requires backend server)
