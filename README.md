# 혜택 플랫폼

## 개요

전사 혜택을 **한 플랫폼의 정책**으로 정규화해 제공한다.

멤버십·쿠폰·포인트·티켓·프로모션을 각 도메인이 제각각 만들지 않는다.  
혜택을 주고 싶으면 **어드민에서 정책을 만들고**, 상품·결제·마케팅 등은 **API로 그 정책을 연동**한다.

| 영역 | 역할 |
|------|------|
| 쿠폰 | 발급·사용·쿠폰함 |
| 포인트 | 적립·사용·소멸 |
| 멤버십 | 등급·구독 자격 |
| 티켓 | 코드 배포·입력 → 쿠폰/포인트 등으로 교환 |
| 프로모션 | 캠페인·한시 혜택 |

- 혜택 로직·한도·정산 규칙을 **한곳**에 모아 중복 개발과 정책 불일치를 줄인다.
- 마케팅·상품·결제·정산·회계가 같은 정책 계약을 쓰게 해 **출시·운영 비용**을 낮춘다.
- 이벤트·광고·홈·자체 지면(멤버십/쿠폰/포인트/이벤트)에 **같은 정책을 재노출**해 캠페인 성과를 키운다.
- 타팀·FE는 환경마다 다른 ID가 아니라 **정책 키**로 연동해 개발·운영 효율을 맞춘다.

## 목표 트래픽

국내 쿠팡급 · 혜택 플랫폼이 받는 쿠폰 트래픽.

발급 **시도** 대부분은 Redis에서 솔드아웃·이미발급으로 끝나고, Mongo에는 **성공 write만** 들어온다.

| 지표 | 평상시 RPS | 피크 RPS | 어디에 닿나 |
|------|-----------:|---------:|-------------|
| 발급 시도 | 1,000 | **30,000** | Redis (inspect/reserve) |
| 발급 성공 write | 50 | 500 | Redis → **Mongo insert** |
| 사용 write | 100 | 800 | Mongo CAS |
| 쿠폰함 조회 | 200 | 1,500 | Mongo |
| Mongo write 합 | 150 | **1,300** | 발급성공+사용 |
| 캠페인 마커 키 | ~50만 | 200만~500만 | Redis 메모리 |

---

## 스택

| 용도 | 선택 |
|------|------|
| 재고·발급 게이트 | Redis 8.x + Lua (`EVALSHA`) |
| 문서 SoR | MongoDB 8.x |
| 이벤트 | Kafka 4.x (동기 API 밖) |
| API | Spring Boot 4, Java 25 |

---

## Redis

선착순 한 캠페인의 30k RPS는 **그 슬롯 primary 1대**가 받는다. Cluster는 핫키를 나눠 주지 않는다.

### 용량

- 명령 처리는 싱글스레드 → **실효 1 core**. 단순 조회 기준 대략 **~10만 RPS/core**.
- 피크 발급 시도 3만 RPS는 조회·짧은 Lua 기준 **primary 1대로 여유**. (Mongo 가기 전 게이트)
- RDB/AOF rewrite 시 메모리 카피용으로 **2 core**까지 씀 → 노드당 **2 core**. 그 이상 core는 무의미.
- 확장은 core가 아니라 **메모리(마커 키)** 와 HA용 **replica 대수**.

| 구성 | core / mem | 대수 | 역할 |
|------|------------|-----:|------|
| Primary | 2 / 16~32GB | 1 | inspect · reserve Lua · 마커 |
| Replica | 2 / 16~32GB | 2 | HA (쓰기·Lua는 primary만) |
| **합** | | **3** | |

```conf
maxmemory-policy noeviction
appendonly yes
appendfsync everysec
```

---

## MongoDB

Redis 통과분 + 사용 write만. 피크 write ~1.3k · 쿠폰함 조회 ~1.5k → **샤딩 불필요**.

| 구성 | core / mem / disk | 대수 |
|------|-------------------|-----:|
| RS (PSS) | 4 / 32GB / 500GB | 3 |

- `w: majority`, 상태 변경 `updateOne` CAS.
- 인덱스: wallets `(userId, status, expiresAt)`, `(userId, policyId)` unique / policies `(code)` unique.

---

## Kafka

감사·후처리. 동기 발급 경로에 넣지 않음.

| 구성 | core / mem / disk | 대수 |
|------|-------------------|-----:|
| Broker | 2 / 8GB / 100GB | 3 |

```properties
replication.factor=3
min.insync.replicas=2
acks=all
enable.idempotence=true
compression.type=lz4
```

---

## 앱

피크 3만 발급 시도는 Redis 왕복이 병목 후보. API는 연결·타임아웃 여유 두고 스케일.

| 역할 | core / mem | 평상시 | 피크 |
|------|------------|-------:|-----:|
| benefit-api | 4 / 16GB | 2 | 6~12 |
| benefit-admin-api | 2 / 8GB | 1 | 2 |
| benefit-consumer | 2 / 8GB | 1 | 2 |
| benefit-batch | 2 / 8GB | 1 | 1 |
| **합** | | **5** | **10~17** |
