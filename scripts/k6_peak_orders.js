import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const QR_TOKEN = __ENV.QR_TOKEN;

export const options = {
  scenarios: {
    peak_tables: {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 20 },
        { duration: '3m', target: 50 },
        { duration: '2m', target: 50 },
        { duration: '1m', target: 0 }
      ]
    }
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.02']
  }
};

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
  if (!QR_TOKEN) {
    throw new Error('QR_TOKEN env var is required');
  }

  const payload = {
    qrToken: QR_TOKEN,
    idempotencyKey: `${__VU}-${__ITER}-${Date.now()}`,
    items: [
      {
        dishId: 'replace-with-real-dish-id',
        quantity: randomInt(1, 3),
        selectedAddOns: []
      }
    ]
  };

  const res = http.post(`${BASE_URL}/api/public/orders`, JSON.stringify(payload), {
    headers: { 'Content-Type': 'application/json' }
  });

  check(res, {
    'status is 200': (r) => r.status === 200
  });

  sleep(1);
}
