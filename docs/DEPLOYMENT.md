# Deployment Setup (Production + Monitoring)

## 1) Prepare env file

```bash
cp deploy/.env.prod.example deploy/.env.prod
```

Set strong values for:
- `MONGO_ROOT_PASSWORD`
- `MONGO_APP_PASSWORD`
- `APP_JWT_SECRET` (base64, >= 32 bytes)
- `APP_QR_SECRET` (base64, >= 32 bytes)
- `APP_AUTH_SETUP_KEY` (required for `/api/auth/register-admin`)

Generate base64 secrets quickly:

```bash
openssl rand -base64 48
```

## 2) Start application stack

```bash
docker compose \
  --env-file deploy/.env.prod \
  -f deploy/docker-compose.prod.yml \
  up -d --build
```

App endpoints:
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`

## 3) Start monitoring stack (Prometheus + Grafana)

```bash
docker compose \
  --env-file deploy/.env.prod \
  -f deploy/docker-compose.prod.yml \
  -f monitoring/docker-compose.monitoring.yml \
  up -d
```

Monitoring endpoints:
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001`

## 4) Backups

Create backup:

```bash
MONGO_ROOT_PASSWORD=... scripts/backup_mongo.sh ./backups
```

Restore backup:

```bash
MONGO_ROOT_PASSWORD=... scripts/restore_mongo.sh ./backups/<file>.archive.gz
```

## 5) Load test peak hours (25-50 tables)

Install k6, then run:

```bash
BASE_URL=http://localhost:8080 \
QR_TOKEN='<table_qr_token>' \
k6 run scripts/k6_peak_orders.js
```

Before test:
- Replace `dishId` in `scripts/k6_peak_orders.js` with a real dish id.

## 6) Realtime operations

Admin realtime stream:
- `GET /api/admin/orders/stream` (SSE)

Kitchen queue:
- `GET /api/admin/orders/kds/queue`

## 7) Security notes

- Keep `/api/auth/register-admin` protected in production (network ACL / temporary onboarding toggle).
- Registration now requires header `X-Setup-Key: <APP_AUTH_SETUP_KEY>` unless `APP_AUTH_ALLOW_PUBLIC_REGISTRATION=true`.
- Use HTTPS and reverse proxy in front of frontend/backend.
- Rotate JWT/QR secrets periodically.
