#!/usr/bin/env bash
set -euo pipefail

BACKUP_DIR=${1:-./backups}
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
mkdir -p "$BACKUP_DIR"

DB_NAME=${MONGO_DB_NAME:-restaurant_db}
CONTAINER=${MONGO_CONTAINER:-menu_mongodb}
ROOT_USER=${MONGO_ROOT_USERNAME:-admin}
ROOT_PASS=${MONGO_ROOT_PASSWORD:-change_me_root}

ARCHIVE="${BACKUP_DIR}/mongo_${DB_NAME}_${TIMESTAMP}.archive.gz"

docker exec "$CONTAINER" mongodump \
  --username "$ROOT_USER" \
  --password "$ROOT_PASS" \
  --authenticationDatabase admin \
  --db "$DB_NAME" \
  --archive \
  --gzip > "$ARCHIVE"

echo "Backup written: $ARCHIVE"
