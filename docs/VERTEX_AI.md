# Using Vertex AI in this Project

You can add Vertex AI for high-impact restaurant workflows without changing core order flow.

## Best use-cases here

1. `Menu intelligence`
- Auto-generate short dish descriptions and tags.
- Suggest upsell add-ons based on dish category.

2. `Ops assistant`
- Explain rush-hour bottlenecks from recent orders.
- Suggest kitchen staffing based on last N hours.

3. `Customer assistant`
- Chat helper for menu discovery (veg, spicy, allergens).

## Recommended integration pattern

- Keep all Vertex calls in backend only (never from browser directly).
- Add a small service layer, e.g. `VertexAiService`.
- Cache responses for repetitive prompts (menu descriptions, analytics summaries).
- Add strict timeout + fallback (never block order APIs on AI failures).

## Auth

Use Google service account credentials on server:

```bash
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json
export GCP_PROJECT_ID=your-project-id
export GCP_REGION=us-central1
```

## Java implementation options

1. Vertex AI Java SDK (preferred)
2. REST call via `WebClient`

## Example features to expose as APIs

- `POST /api/admin/ai/menu/enrich`
  - Input: dish list
  - Output: rewritten descriptions + tags + upsell suggestions

- `GET /api/admin/ai/rush-hour-summary`
  - Input: tenantId + date range
  - Output: short actionable summary for manager

## Safety and cost controls

- Per-tenant request quotas.
- Prompt templates + output schema validation.
- Do not send sensitive PII to model.
- Track token usage in metrics.

## Suggested first rollout (low risk)

Start with `Sales summary assistant` in admin stats section:
- Reads existing order data.
- Generates short textual insight.
- No impact on order placement path.
