# Unified Server Dashboard for Android

Native Kotlin/Jetpack Compose client for a Unified Server Dashboard backend.

## Development

Use JDK 17 (the Mac setup has also been verified with Temurin 21), Android SDK
platform 34 and build-tools 34.0.0. Gradle 8.6 is provided by the wrapper.
Set `ANDROID_HOME`, or put `sdk.dir=/path/to/android-sdk` in an untracked
`local.properties`. Do not commit API keys, local settings or build outputs.

```sh
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleRelease
adb devices
adb -s DEVICE_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
adb -s DEVICE_SERIAL shell am start -W -n com.unifiedserver.dashboard/.MainActivity
adb -s DEVICE_SERIAL shell pidof com.unifiedserver.dashboard
adb -s DEVICE_SERIAL logcat --pid=APP_PID
```

Use the same signing key for repeated installations. GitHub Actions' disposable
debug keystore is not a stable deployment identity: CI APKs may not update a
locally signed installation. For daily USB iteration build on the same Mac.
Release builds are minified but unsigned; production signing and store publishing
are not configured by this workflow.

## User journeys

- **Home**: health, last update, download previews and server summaries.
- **Media**: Movies, TV discovery and Tracked shows.
- **Downloads**: source search, paginated results and active transfers.
- **Server**: resources, services, all storage devices and confirmed power actions.
- **Settings**: accessible from screen toolbars; server URLs are tested before saving.

“Find sources” passes a title/year to Downloads, which owns the search operation.
It does not immediately download a file. Select a source to add it.

TMDB is optional for server management but required for media discovery.
An optional personal OMDb key enables IMDb ratings. Keys are held in the app's
private preferences; request bodies are not logged. Configure only a trusted
backend. This repository does not establish or audit backend authentication.
HTTPS is the default; the network-security XML has explicit legacy local-host
HTTP exceptions, not a private-network wildcard.

## Validation

Local JVM tests cover URL validation, action-response handling, setup/preferences,
search ownership/cancellation, settings and display transformations.
CI runs unit tests, lint and both build variants and retains reports and a debug
APK. Live server mutations are deliberately not exercised by automated tests.

Before a release, also test on a disposable fresh installation and with TalkBack,
large fonts, rotation, offline/reconnection, different screen sizes and the actual
backend version. In particular verify power actions and destructive operations
only against a test server.
