#!/usr/bin/env bash
# One-time, idempotent setup of the self-hosted F-Droid repo on the home server.
# Run from the Mac:   ssh orangepi 'bash -s' < scripts/setup-orangepi.sh
#
# Repo data lives on the SSD (not the SD card), outside the media library that the
# dashboard's Storage tab scans:
#   /mnt/storage/fdroid/config.yml, keystore.p12   repo index signing key (never served)
#   /mnt/storage/fdroid/incoming/                  uploads in progress (*.part)
#   /mnt/storage/fdroid/repo/                      served at http://192.168.0.30/fdroid/repo
set -euo pipefail

STORAGE=/mnt/storage
FDROID_DIR=$STORAGE/fdroid
REPO_URL=http://192.168.0.30/fdroid/repo
NGINX_SITE=/etc/nginx/sites-available/default
NGINX_SNIPPET=/etc/nginx/snippets/fdroid.conf

[ "$(id -u)" -eq 0 ] || { echo "Run as root." >&2; exit 1; }
mountpoint -q "$STORAGE" || { echo "The SSD is not mounted at $STORAGE; refusing to write to the SD card." >&2; exit 1; }

# 1. fdroidserver + the headless JDK it signs with (no desktop Java/GUI packages).
if ! command -v fdroid >/dev/null || ! command -v jarsigner >/dev/null; then
  apt-get update -qq
  DEBIAN_FRONTEND=noninteractive apt-get install -y -qq --no-install-recommends \
    fdroidserver openjdk-17-jdk-headless
fi

# 2. Repo + index signing key. `fdroid init` creates config.yml and keystore.p12 once;
#    losing that keystore means re-adding the repo on every phone.
mkdir -p "$FDROID_DIR/incoming" "$FDROID_DIR/repo"
cd "$FDROID_DIR"
[ -f config.yml ] || fdroid init
chmod 600 config.yml keystore.p12

# 3. Our settings, replaced on every run so the script stays the source of truth.
python3 - "$REPO_URL" <<'PY'
import re, sys
url = sys.argv[1]
settings = {
    "repo_url": url,
    "repo_name": "Home apps",
    "repo_description": "Builds from the MacBook, served on the home LAN.",
    "archive_older": "3",
}
text = open("config.yml").read()
for key in settings:
    text = re.sub(rf"(?m)^{key}:.*\n", "", text)
lines = [f"{k}: {v}\n" if v.isdigit() else f'{k}: "{v}"\n' for k, v in settings.items()]
text = text.rstrip("\n") + "\n" + "".join(lines)
open("config.yml", "w").write(text)
PY
fdroid update --create-metadata >/dev/null

# 4. nginx: serve only repo/ (never config.yml or the keystore) on the existing site.
cat > "$NGINX_SNIPPET" <<NGINX
# Self-hosted F-Droid repo (scripts/setup-orangepi.sh in the Android app repo).
location /fdroid/repo/ {
    alias $FDROID_DIR/repo/;
    autoindex off;
}
NGINX
if ! grep -q "snippets/fdroid.conf" "$NGINX_SITE"; then
  cp "$NGINX_SITE" "$NGINX_SITE.bak-fdroid"
  # Include it inside the default server block, right after its server_name line.
  sed -i '0,/^\s*server_name .*;/s//&\n\tinclude snippets\/fdroid.conf;/' "$NGINX_SITE"
fi
nginx -t -q
systemctl reload nginx

echo "Repo ready: $REPO_URL"
