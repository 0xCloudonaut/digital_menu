#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <archive-file>"
  exit 1
fi

ARCHIVE_FILE=$1
DB_NAME=${MONGO_DB_NAME:-restaurant_db}
CONTAINER=${MONGO_CONTAINER:-menu_mongodb}
ROOT_USER=${MONGO_ROOT_USERNAME:-admin}
ROOT_PASS=${MONGO_ROOT_PASSWORD:-change_me_root}

test -f "$ARCHIVE_FILE"

gunzip -c "$ARCHIVE_FILE" | docker exec -i "$CONTAINER" mongorestore \
  --username "$ROOT_USER" \
  --password "$ROOT_PASS" \
  --authenticationDatabase admin \
  --archive \
  --drop

echo "Restore completed from: $ARCHIVE_FILE"
