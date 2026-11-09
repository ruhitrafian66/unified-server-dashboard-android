#!/usr/bin/env bash
# Build a signed release on this Mac and publish it to the self-hosted F-Droid repo
# on the home server. Phones running F-Droid with that repo pick the update up.
#
#   git ship          # push master, then run this script (alias, see README)
#   scripts/deploy.sh # deploy the already-pushed master
#
# Only a clean, pushed master is deployed, so phones only get code that is on GitHub.
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"

PACKAGE=com.unifiedserver.dashboard
KEYSTORE=$HOME/.android/keystores/unified-dashboard-release.jks
KEYCHAIN_SERVICE=unified-dashboard-release-keystore
# SHA-256 of the release certificate. Every build must carry it, or phones can't update.
EXPECTED_CERT=9a2c61527c7a881e38f4bfa61f5f25947b17fca6e11c8853b71ee331db9b3a41
SERVER=orangepi
FDROID_DIR=/mnt/storage/fdroid
REPO_URL=http://192.168.0.30/fdroid/repo

say() { printf '\033[1m==> %s\033[0m\n' "$*"; }
die() { printf 'deploy: %s\n' "$*" >&2; exit 1; }

# ─── Preconditions ────────────────────────────────────────────────────────────
[ "$(git branch --show-current)" = master ] || die "check out master first (only master is deployed)."
[ -z "$(git status --porcelain --untracked-files=no)" ] || die "commit or stash your changes first."
git fetch -q origin master
[ "$(git rev-parse HEAD)" = "$(git rev-parse origin/master)" ] || die "master is not pushed (try: git ship)."
[ -f "$KEYSTORE" ] || die "release keystore missing: $KEYSTORE"
KS_PASS=$(security find-generic-password -a "$USER" -s "$KEYCHAIN_SERVICE" -w) \
  || die "keystore password not found in the Keychain ($KEYCHAIN_SERVICE)."
ssh -o BatchMode=yes -o ConnectTimeout=8 "$SERVER" "mountpoint -q /mnt/storage && test -d $FDROID_DIR/repo" \
  || die "$SERVER unreachable, or its SSD/repo is missing (run scripts/setup-orangepi.sh)."

export JAVA_HOME=${JAVA_HOME:-$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home)}
SDK_DIR=$(sed -n 's/^sdk.dir=//p' local.properties 2>/dev/null || true)
SDK_DIR=${SDK_DIR:-${ANDROID_HOME:-$HOME/android-sdk}}
APKSIGNER=$(ls -d "$SDK_DIR"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -1)
[ -x "$APKSIGNER" ] || die "apksigner not found under $SDK_DIR/build-tools."

# Commit count on master only ever grows, so F-Droid always sees a newer build.
VC=$(git rev-list --count HEAD)
NAME="${PACKAGE}_${VC}.apk"
SHA=$(git rev-parse --short HEAD)

# ─── Build ───────────────────────────────────────────────────────────────────
say "Testing and building $PACKAGE versionCode $VC ($SHA)"
RELEASE_KEYSTORE=$KEYSTORE RELEASE_KEYSTORE_PASSWORD=$KS_PASS RELEASE_KEY_ALIAS=release \
  ./gradlew -q :app:testDebugUnitTest :app:assembleRelease -PversionCode="$VC"
APK=app/build/outputs/apk/release/app-release.apk
[ -f "$APK" ] || die "signed APK not produced (is signing configured?)."

CERT=$("$APKSIGNER" verify --print-certs "$APK" | sed -n 's/.*certificate SHA-256 digest: //p' | head -1)
[ "$CERT" = "$EXPECTED_CERT" ] || die "APK is signed with an unexpected certificate ($CERT); not publishing."

# ─── Publish ─────────────────────────────────────────────────────────────────
say "Uploading $NAME to $SERVER"
# .part + mv: fdroid update never sees a half-copied APK; flock: one index update at a time.
scp -q -o BatchMode=yes "$APK" "$SERVER:$FDROID_DIR/incoming/$NAME.part"
ssh -o BatchMode=yes "$SERVER" "set -e; cd $FDROID_DIR
  mv incoming/$NAME.part repo/$NAME
  flock /tmp/fdroid.lock fdroid update --create-metadata -q"

curl -fsS "$REPO_URL/index-v2.json" | grep -Eq "\"versionCode\": ?$VC[^0-9]" \
  || die "uploaded, but the repo index does not list versionCode $VC yet."
say "Published versionCode $VC to $REPO_URL — phones update from F-Droid."
