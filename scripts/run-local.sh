#!/usr/bin/env bash
set -euo pipefail

# Determine repository root (parent of this script's directory) and cd there
SCRIPT_DIR="$(cd -- "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
REPO_ROOT="${SCRIPT_DIR%/scripts}"
cd "$REPO_ROOT"

# Load .env if present and export all variables
if [ -f .env ]; then
  echo "Loading environment from .env"
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
else
  echo ".env not found. You can copy .env.example to .env and set your variables."
fi

# Run the app
./mvnw -DskipTests spring-boot:run
