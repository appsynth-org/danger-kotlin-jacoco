#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

function cleanup {
  echo "Cleanup..."
  rm -f gradle.properties signing-key.asc
}

trap cleanup SIGINT SIGTERM ERR EXIT

echo "Decrypting files..."

gpg --quiet --batch --yes --decrypt --passphrase="${GPG_SECRET}" --output signing-key.asc .ci/signing-key.asc.gpg
gpg --quiet --batch --yes --decrypt --passphrase="${GPG_SECRET}" --output gradle.properties .ci/gradle.properties.gpg
gpg --fast-import --no-tty --batch --yes signing-key.asc

echo "Publishing..."

./gradlew publish

echo "Done!"
