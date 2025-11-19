#!/usr/bin/env bash
set -euo pipefail

CURRENT_VERSION=$(awk -F'[<>]' '/<revision>/ {print $3; exit}' pom.xml)
echo "Current version: '$CURRENT_VERSION'"
echo "current_version=$CURRENT_VERSION" >> "$GITHUB_ENV"

if [[ "$CURRENT_VERSION" == *-SNAPSHOT ]]; then
    echo "::warning::Version '$CURRENT_VERSION' is a SNAPSHOT"
    echo "skip=true" >> "$GITHUB_ENV"
    echo "skip_reason=snapshot" >> "$GITHUB_ENV"
elif PREVIOUS_VERSION=$(git describe --tags --abbrev=0 2>/dev/null); then
    echo "Previous version: '$PREVIOUS_VERSION'"
    if [[ "v$CURRENT_VERSION" == "$PREVIOUS_VERSION" ]]; then
        echo "::warning::Version number has not changed from $PREVIOUS_VERSION"
        echo "skip=true" >> "$GITHUB_ENV"
        echo "skip_reason=unchanged" >> "$GITHUB_ENV"
    else
        echo "skip=false" >> "$GITHUB_ENV"
    fi
else
    echo "No previous tags found, first release"
    echo "skip=false" >> "$GITHUB_ENV"
fi
