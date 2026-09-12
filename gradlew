#!/usr/bin/env bash
# This wrapper script is intentionally minimal; it is generated for Android project bootstrap.
# It uses the installed Gradle distribution and will be available once the project is initialized.

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec gradle "$@"
