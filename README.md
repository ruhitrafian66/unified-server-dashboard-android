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
CI release builds are minified but unsigned.

## Delivery to phones (self-hosted F-Droid)

Phones get releases from an F-Droid repo on the home server, not over adb:

```
git ship   →  Mac: tests + signed assembleRelease (versionCode = commits on master)
           →  scp to orangepi:/mnt/storage/fdroid  →  fdroid update (signed index)
           →  nginx http://192.168.0.30/fdroid/repo  →  F-Droid on each phone
```

- `git ship` is a repo-local alias: `git config alias.ship '!git push origin master && scripts/deploy.sh'`.
  `scripts/deploy.sh` only deploys a clean `master` that is already on GitHub, and
  refuses an APK not signed with the release certificate
  (SHA-256 `9a2c6152…db9b3a41`).
- **App signing key:** `~/.android/keystores/unified-dashboard-release.jks`, password in the
  macOS Keychain (service `unified-dashboard-release-keystore`). Never in git.
- **Repo index key:** `/mnt/storage/fdroid/keystore.p12` on the server (copy in
  `~/.android/keystores/fdroid-repo-backup/`). Fingerprint
  `D7EAF236D8639B92E178341068442BBE8A268A004F725DDCD20557C9DE9351A5`.
- **Back up both keys and the Keychain entry off this Mac.** Losing either one means
  uninstalling and reinstalling the app on every phone.
- Server setup is idempotent: `ssh orangepi 'bash -s' < scripts/setup-orangepi.sh`.
  Repo data lives on the SSD, outside the `Server/` media library.

Adding a phone (once): install the F-Droid client, then add the repo
`http://192.168.0.30/fdroid/repo?fingerprint=D7EAF236D8639B92E178341068442BBE8A268A004F725DDCD20557C9DE9351A5`
(Settings › Repositories › +). A copy installed from Android Studio or adb is signed
with a debug key: uninstall it first (this clears its saved server URL). Updates
arrive when F-Droid refreshes; pull to refresh to check immediately.

## User journeys

- **Home**: health, last update, resources, VPN and qBittorrent status.
- **Media**: Movies, TV discovery, Tracked shows and Torrents (source search,
  paginated results, magnet links).
- **Storage**: the server's download folder as a media library (`/api/library`):
  clean movie/TV entries with posters, seasons and episodes, duplicate and partial
  detection, junk cleanup and delete (which also removes the torrent).
- **Downloads bar**: shown above the navigation while torrents download; cycles
  through several (paused while touched); tap to stop, start or remove.
- **Server**: resources, temperature, media and system storage, VPN and qBittorrent
  status, services, containers and confirmed power actions.
- **Settings**: accessible from screen toolbars; server URLs are tested before saving.

“Find sources” is always manual: it passes the title/year (or title/season) to
Media › Torrents and presents sources for you to select. It never automatically adds a file.

“Auto-download…” is a separate opt-in action with confirmation. Its rules require
1080p or higher (optionally 4K), at least one seeder, a known file size and a
filename matching the title plus movie year or TV season. An optional maximum
size can be set. Eligible sources are ranked by seeders across resolutions.
There is no silent lower-quality fallback. Filename metadata is a heuristic,
not a guarantee about actual file contents; choose manually when uncertain.

Automatic searches wait for completed server results and inspect all reported
pages, up to 2,000 results and a five-minute overall deadline. Incomplete or
oversized result sets require manual selection. A persistent Torrents card
shows searching, selection, adding, success, failure and cancellation. Once an
add request is sent, it cannot be safely cancelled or automatically retried:
check the downloads bar first if the network outcome is uncertain.

Settings and Tracked shows share one server-confirmed automation snapshot.
Failed refreshes retain known values and expose retry without changing them.

The app targets the backend's current API (unified-server-dashboard `7474439`):
every write sends `X-Requested-With: dashboard` (the backend's CSRF guard), and
Home/Server read one `/api/system/overview` call that tolerates partial failures.

TMDB is optional for server management but required for media discovery. Its key
is stored on the server (Settings → TMDB, verified before saving) and all TMDB
requests go through the backend's `/api/tmdb` proxy, so it works on every device.
Movies lists films newly out on digital/streaming, not cinema-only trending.
An optional personal OMDb key enables IMDb ratings; it is held in the app's
private preferences. Request bodies are not logged. Configure only a trusted
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
