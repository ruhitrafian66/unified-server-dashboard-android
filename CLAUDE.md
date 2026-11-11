# Unified Server Dashboard for Android

Native Kotlin/Compose client for the owner's home-server dashboard. The web dashboard and
its backend are the upstream project; this app follows them. README.md covers user
journeys, signing keys and the F-Droid setup in detail; this file is what you need to
work here.

## Upstream

- Upstream: https://github.com/ruhitrafian66/unified-server-dashboard (backend in
  `backend/`, web UI in `frontend/src/`). A local clone is usually at
  `~/Unified Server Dashboard`; `git fetch` it rather than trusting its checkout.
- "Sync with upstream" means: port what changed since the last synced backend commit.
  That commit is named in the newest `sync with backend <sha>` commit subject and in
  README.md ("The app targets the backend's current API"). Update both when you sync.
- Read `git diff <last-sha> origin/HEAD` upstream. Port API changes from `backend/routes/`
  and the matching UI behaviour from `frontend/src/pages/` and `components/`. Match the
  web dashboard's wording, thresholds and fallbacks unless a phone needs something else.
- The live server usually runs the newest backend already. Check with
  `curl -s http://dash.local/api/system/overview` (`deploy.commit`) and read real
  responses before writing models.
- Keep working against older backends: new response fields are nullable or defaulted,
  and the UI falls back when they are absent.

## Build and test

The default JDK on this Mac is 24, which breaks the build. Always use 21:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew -q :app:testDebugUnitTest          # JVM tests; must pass before shipping
./gradlew -q :app:assembleRelease            # minified (R8); what phones actually run
```

- The Android SDK is at `~/android-sdk` (`local.properties`, untracked); `adb` is
  `~/android-sdk/platform-tools/adb`. There is no emulator or system image installed.
- The shell is zsh on macOS: quote globs (`--include='*.kt'`), and `sed -i ''` is BSD sed.
- Release builds are minified. A change that works in debug can still crash in release
  (reflection, Gson models). Models under `data/models/` are kept by
  `app/proguard-rules.pro`; anything else reached by reflection needs its own keep rule.

## Shipping

`git ship` (a repo-local alias: `git push origin master && scripts/deploy.sh`) is the only
way builds reach the phones. It runs the unit tests, builds a signed release with
versionCode = commit count, checks the signing certificate, uploads to the F-Droid repo
on the server and confirms the index lists the new version. Phones update from F-Droid.

- It deploys only a clean, pushed `master`. Commit first; work directly on `master`.
- Ship only when the owner asks for it. Pushing publishes to a public GitHub repo.
- Never put keystores, passwords or API keys in git. The keystore password is read from
  the macOS Keychain by `scripts/deploy.sh`.
- No GitHub self-hosted runner: the repo is public.
- `handoff-android-fdroid-pipeline.md` is a leftover note; leave it untracked.

## Checking changes for real

- Unit tests cover view models and pure helpers. Repositories are `open` so tests
  subclass them as fakes (see `StorageAndDownloadsTest`, `MetricHistoryTest`); there is
  no mocking library.
- On a phone: if `adb devices` shows one, install the signed release over the F-Droid
  copy with the *installed* versionCode (same key, so `adb install -r` works), then
  launch and read `adb logcat -b crash -d`. The phone is often locked or off adb; say so
  rather than claiming a visual check.
- To see a composable without a phone, add Paparazzi temporarily: plugin
  `id("app.cash.paparazzi") version "1.3.4"` in `app/build.gradle.kts`, a snapshot test
  under `app/src/test/`, then
  `ANDROID_HOME=~/android-sdk ./gradlew :app:recordPaparazziDebug --tests '*YourShot*'`
  and read the PNG in `app/src/test/snapshots/images/`. Remove the plugin, test and
  snapshots before committing.

## The server

- `ssh orangepi` logs in as root on the Orange Pi (Debian 12, arm64) at 192.168.0.30,
  also reachable as `dash.local`. It is the owner's live home server: read freely, but
  ask before changing or deleting anything on it.
- nginx on :80 proxies the dashboard backend (node, 127.0.0.1:3001) and serves the
  F-Droid repo at `/fdroid/repo` from `/mnt/storage/fdroid`.
- qBittorrent runs in Docker (`docker exec qbittorrent curl -s http://127.0.0.1:8080/api/v2/...`).
  The media library is `/mnt/storage/Server`, mounted in the container as `/downloads`.

## Code map

Package `com.unifiedserver.dashboard`, under `app/src/main/java/`:

- `data/ApiService.kt`: every backend endpoint (Retrofit). `data/models/Models.kt`: all
  response models, Gson, every field defaulted.
- `data/repository/`: one repository per area, returning `Result<T>` through `apiResult {}`.
  `RetrofitFactory.kt` builds the client and adds `X-Requested-With: dashboard` to
  writes (the backend's CSRF guard). `DownloadsMonitor` is the app-wide torrent list.
- `ui/screens/`: a screen and a Hilt view model per destination, state as one
  `data class …UiState` in a `StateFlow`.
- `ui/components/`: shared composables plus the pure helpers they render
  (`OverviewComponents.kt`, `HistoryCard.kt`). Put logic worth testing in plain
  functions there or in `ui/utils/`, not inside composables.
- `ui/navigation/AppNavigation.kt`: bottom bar (Home, Media, Storage, Server), the Media
  tab row, `AppTopBar`, and the downloads bar above the navigation bar.
- `res/xml/network_security_config.xml`: plain HTTP is allowed only for the listed LAN
  hosts and `.local` names. A new server address that is not HTTPS must be added here or
  Android refuses it with "cleartext communication not permitted".

## Conventions

- Screens refresh with `RefreshWhileVisible(intervalMs)`; a failed refresh keeps the
  previous data and shows an error line rather than blanking the screen.
- Guard against stale responses when the user can change what is being loaded (range,
  search, selection): apply a result only if it still matches the current request.
- Comments say why, briefly. Commit subjects use `feat:`, `fix:`, `style:`, `ci:`.
- Colours and status levels come from `ui/theme/` and `levelColor`/`healthColor`; over
  65% is yellow and over 85% is red everywhere.
