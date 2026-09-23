# Benefit

A commerce benefit platform designed to make core benefit capabilities easy to use — coupons, points, memberships, and promotions — behind clear APIs and domain boundaries.

## Modules

| Module | Role |
|--------|------|
| `benefit-api-application` | App API (`dto` request/response) |
| `benefit-admin-api-application` | Admin API (`dto` request/response) |
| `benefit-domain-module` | Domain rules (coupon / point / membership / promotion) |
| `benefit-data-module` | `entity` (documents / types) · `infrastructure` (keys/config + `<domain>` callers) |
| `benefit-consumer-application` | Kafka listeners |
| `benefit-batch-application` | Batch jobs |

## Disclaimer

Much of the business logic in this repository was written with AI assistance.

- **Do not fully trust** implementations, edge-case handling, or runtime behavior.
- Prefer treating **class- and method-level design** as the useful reference.
- Always review, test, and harden code before production use.

## Local Infrastructure

```bash
docker compose up -d
```

## Production Infrastructure Spec

Benefit domain (coupon / point / membership / promotion).

- **Write-heavy**; steady read is low. Product-list coupons are pushed via Kafka.
- **Mongo / Redis**: dedicated benefit clusters (not shared with other domains).
- **Kafka**: company-wide shared bus — table figures are the **benefit (coupon) share only**, not the full cluster.
- Redis stock / idempotency: proven **2 vCPU, 4GB** held time-attack at tens of millions of users.
- **Peak** (time-attack / flash claim): handled operationally — scale App first; tables are baseline, not peak sizing.
- From **Large**: consider a **separate peak App API** application (claim/grant hot paths) isolated from the steady App API.

| Tier | Label | Users | Compute platform |
|------|-------|-------|------------------|
| S0 | Bootstrap | early / local | Docker |
| S1 | Small | ≥ 100K | Docker |
| S2 | Mid | ≥ 1M | Docker |
| S3 | Large | ≥ 10M | Kubernetes |
| S4 | Mega | ≥ 50M | Kubernetes |

**Batch**: not always-on — a container starts per job run, then exits. Spec = request/limit for that run.  
**Admin API**: 1 through Mid; **2 pods from Large**. Multi-AZ from **S2+**.  
**Consumer**: always **2 vCPU, 2GB** per instance; scale with **partition / replica count**.  
**Kafka (benefit share)**: **1 broker-equivalent through Mid**; **3 from Large** — capacity attributed to coupon/benefit traffic on the shared cluster.

### S0 — Bootstrap · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 1 | 2 vCPU, 2GB |
| Admin API | share App or 1 | 2 vCPU, 2GB |
| Consumer | skip or share | — |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo (benefit) | 1 standalone | 2 vCPU, 2GB |
| Redis (benefit) | 1 standalone | 1 vCPU, 1GB |
| Kafka (benefit share) | 1 broker-eq. | 2 vCPU, 2GB |

### S1 — Small (≥ 100K) · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 2 | 2 vCPU, 2GB |
| Admin API | 1 | 2 vCPU, 2GB |
| Consumer | 1 | 2 vCPU, 2GB |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo (benefit) | RS **PSS** (3) | 2 vCPU, 4GB, SSD |
| Redis (benefit) | Primary + replica | 2 vCPU, 2GB |
| Kafka (benefit share) | 1 broker-eq. | 2 vCPU, 2GB, SSD |

### S2 — Mid (≥ 1M) · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 3 | 2 vCPU, 2GB |
| Admin API | 1 | 2 vCPU, 2GB |
| Consumer | 1 | 2 vCPU, 2GB |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo (benefit) | RS **PSS** (3) | 2 vCPU, 8GB, SSD |
| Redis (benefit) | Primary + replica | 2 vCPU, 4GB |
| Kafka (benefit share) | 1 broker-eq. | 2 vCPU, 4GB, SSD |

### S3 — Large (≥ 10M) · Kubernetes

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 6 pods | 2 vCPU, 4GB |
| Admin API | 2 pods | 2 vCPU, 2GB |
| Consumer | 3 pods | 2 vCPU, 2GB |
| Batch | Job / CronJob per run | 2 vCPU, 2GB |
| Mongo (benefit) | RS **PSS** (3) | 4 vCPU, 16GB, SSD |
| Redis (benefit) | Primary + replica | 2 vCPU, 4GB |
| Kafka (benefit share) | 3 broker-eq. | 2 vCPU, 4GB, SSD · RF=3 |

Mongo: prefer one RS; shard only if primary write IO saturates on `userId`-keyed ledgers.

### S4 — Mega (≥ 50M) · Kubernetes

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 12 pods | 2 vCPU, 4GB |
| Admin API | 2 pods | 2 vCPU, 2GB |
| Consumer | 4 pods | 2 vCPU, 2GB |
| Batch | Job / CronJob per run | 2 vCPU, 4GB |
| Mongo (benefit) | **2 shards × 3** (PSS) or larger single RS | 4 vCPU, 32GB, NVMe |
| Mongo config / mongos | 3 config; 1 mongos/AZ if sharded | 2 vCPU, 2GB |
| Redis (benefit) | Cluster **3 masters + 3 replicas** | 2 vCPU, 4GB |
| Kafka (benefit share) | 3 broker-eq. | 2 vCPU, 4GB, NVMe · RF=3 |

Redis: Mega runs many concurrent campaigns — 3 masters spread stock keys. Per-node size stays at the proven time-attack class (2 vCPU, 4GB).

### Topology (S2+)

```
Clients → App API ─┬─ Redis (benefit)     stock / idempotency
                   ├─ Mongo (benefit)     ledger / wallets
                   └─ Kafka (shared) ──► Consumers → Mongo / Redis
                         └ benefit share ≈ coupon traffic only
Batch ── on-demand container (Docker run / K8s Job)
Admin ── 2 pods from Large
```

### Scale cues

| Signal | Action |
|--------|--------|
| Claim p99 ↑ | More App instances/pods; from Large consider peak App API split |
| Redis hot on time-attack | Check stock path; S1–S3 rarely > 2 vCPU, 4GB primary |
| Mongo primary write IO high | Bigger RS, then shard on `userId` |
| Consumer lag | More replicas (match partitions); do not upsize pods |
| Do **not** | Size Admin by user count; keep Batch always-on; treat Kafka rows as full shared cluster size |
