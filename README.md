# Benefit

A commerce benefit platform designed to make core benefit capabilities easy to use — coupons, points, memberships, and promotions — behind clear APIs and domain boundaries.

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
- Redis = stock / idempotency. Proven: **2 vCPU, 4GB** held time-attack at tens of millions of users.
- Mongo = system of record. Kafka = outbound / async side-effects.

| Tier | Label | Users | Compute platform |
|------|-------|-------|------------------|
| S0 | Bootstrap | early / local | Docker |
| S1 | Small | ≥ 100K | Docker |
| S2 | Mid | ≥ 1M | Docker |
| S3 | Large | ≥ 10M | Kubernetes |
| S4 | Mega | ≥ 50M | Kubernetes |

**Batch**: not always-on — a container starts per job run, then exits. Spec = request/limit for that run.  
**Admin API**: 1 through Mid; **2 pods from Large**. Multi-AZ from **S2+**.

### S0 — Bootstrap · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 1 | 2 vCPU, 2GB |
| Admin API | share App or 1 | 2 vCPU, 2GB |
| Consumer | skip or share | — |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo | 1 standalone | 2 vCPU, 2GB |
| Redis | 1 standalone | 1 vCPU, 1GB |
| Kafka | 1 (KRaft combined) | 2 vCPU, 2GB |

### S1 — Small (≥ 100K) · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 2 | 2 vCPU, 2GB |
| Admin API | 1 | 2 vCPU, 2GB |
| Consumer | 1 | 2 vCPU, 2GB |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo | RS **PSS** (3) | 2 vCPU, 4GB, SSD |
| Redis | Primary + replica | 2 vCPU, 2GB |
| Kafka | 3 brokers (KRaft) | 2 vCPU, 2GB, SSD · RF=3, `min.insync=2` |

### S2 — Mid (≥ 1M) · Docker

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 3 | 2 vCPU, 2GB |
| Admin API | 1 | 2 vCPU, 2GB |
| Consumer | 1 | 2 vCPU, 2GB |
| Batch | run container on demand | 2 vCPU, 2GB |
| Mongo | RS **PSS** (3) | 2 vCPU, 8GB, SSD |
| Redis | Primary + replica | 2 vCPU, 4GB |
| Kafka | 3 brokers | 2 vCPU, 4GB, SSD · RF=3 |

### S3 — Large (≥ 10M) · Kubernetes

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 6 pods | 2 vCPU, 4GB |
| Admin API | 2 pods | 2 vCPU, 2GB |
| Consumer | 3 pods | 2 vCPU, 2GB |
| Batch | Job / CronJob per run | 2 vCPU, 2GB |
| Mongo | RS **PSS** (3) | 4 vCPU, 16GB, SSD |
| Redis | Primary + replica | 2 vCPU, 4GB |
| Kafka | 3 brokers | 2 vCPU, 8GB, SSD · RF=3 |

Mongo: prefer one RS; shard only if primary write IO saturates on `userId`-keyed ledgers.

### S4 — Mega (≥ 50M) · Kubernetes

| Layer | Shape | Spec (each) |
|-------|--------|-------------|
| App API | 12 pods | 2 vCPU, 4GB |
| Admin API | 2 pods | 2 vCPU, 2GB |
| Consumer | 4 pods | 2 vCPU, 4GB |
| Batch | Job / CronJob per run | 2 vCPU, 4GB |
| Mongo | **2 shards × 3** (PSS) or larger single RS | 4 vCPU, 32GB, NVMe |
| Mongo config / mongos | 3 config; 1 mongos/AZ if sharded | 2 vCPU, 2GB |
| Redis | Primary + replica | 2 vCPU, 4GB |
| Kafka | 3 brokers | 4 vCPU, 8GB, NVMe · RF=3 |

Redis stays 2 vCPU, 4GB — scale App pods for claim writers, not Redis.

### Topology (S2+)

```
Clients → App API ─┬─ Redis (stock / idempotency)
                   ├─ Mongo
                   └─ Kafka → Consumers → Mongo / Redis
Batch ── on-demand container (Docker run / K8s Job)
Admin ── 2 pods from Large
```

### Scale cues

| Signal | Action |
|--------|--------|
| Claim p99 ↑ | More App instances/pods |
| Redis hot on time-attack | Check stock path; rarely > 2 vCPU, 4GB |
| Mongo primary write IO high | Bigger RS, then shard on `userId` |
| Consumer lag | More consumer replicas |
| Do **not** | Size Redis/Admin by user count; keep Batch always-on |
