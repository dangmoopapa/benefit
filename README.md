# 혜택 플랫폼

전사 혜택(쿠폰·포인트·멤버십·티켓·프로모션)을 **정책 단위로 정규화**해 제공하는 플랫폼이다.  
어드민에서 정책을 만들고, 상품·결제·마케팅은 API로 그 정책을 연동한다.

| 영역 | 제공 |
|------|------|
| 쿠폰 | 발급 · 사용 · 쿠폰함 |
| 포인트 | 적립 · 사용 · 소멸 |
| 멤버십 | 등급 · 구독 자격 |
| 티켓 | 코드 배포 · 교환 |
| 프로모션 | 캠페인 · 한시 혜택 |

## 모듈

| 모듈 | 역할 | 기본 포트 |
|------|------|----------:|
| `benefit-domain` | 도메인 · Redis/Mongo/Kafka 공통 | — |
| `benefit-api` | 사용자 API | 8080 |
| `benefit-admin-api` | 운영 API | 8081 |
| `benefit-consumer` | 이벤트 소비 | — |
| `benefit-batch` | 배치 Job (K8s) | — |

스택: **Java 25 · Spring Boot 4.1 · Redis 8 · MongoDB 8 · Kafka 4**

## 용량 가정

용량 **계획용** 가정이다. 벤치마크 확정값이 아니며, 캠페인·연동 구조에 따라 달라진다.

| 지표 | 평상시 | 피크 | 1차 부하 |
|------|-------:|-----:|----------|
| 발급 시도 | 1,000 RPS | 30,000 RPS | Redis |
| 발급 성공 | 50 RPS | 500 RPS | Mongo insert |
| 사용 | 100 RPS | 800 RPS | Mongo CAS (+ Redis 한도) |
| 쿠폰함 조회 | 200 RPS | 1,500 RPS | Mongo |
| 정책당 유저 마커 | ~50만 키 | 200만~500만 키 | Redis 메모리 |

---

## Redis

역할: 발급 게이트(재고·1인 1회 마커)와 정책 총 사용 카운터.

| | |
|--|--|
| 구성 | Primary 1 + Replica 2 |
| 노드 | 2 vCPU / 16~32 GB |
| 스크립트 | classpath Lua → Spring `RedisScript` (`EVALSHA`, `NOSCRIPT` 시 `EVAL`) |

**왜 이렇게 두나**

- 명령 실행은 대체로 단일 스레드다. 코어를 늘려도 명령 처리량은 선형으로 안 오른다. 여유 코어는 RDB/AOF rewrite·복제용으로 본다. → [Latency diagnostics](https://redis.io/docs/latest/operate/oss_and_stack/management/optimization/latency/), [Benchmarks](https://redis.io/docs/latest/operate/oss_and_stack/management/optimization/benchmarks/)
- 재고 키와 유저 마커는 한 스크립트에서 같이 본다. Cluster를 쓰면 **같은 hash slot**이어야 한다(`{policyId}` 등). → [Cluster specification — Hash tags](https://redis.io/docs/latest/operate/oss_and_stack/reference/cluster-spec/#hash-tags)
- 마커·카운터는 캐시가 아니라 게이트다. 메모리 부족 시 키를 지우지 않는다. → [Eviction — `noeviction`](https://redis.io/docs/latest/develop/reference/eviction/)
- 내구성은 AOF, fsync는 기본 권장인 초당. → [Persistence](https://redis.io/docs/latest/operate/oss_and_stack/management/persistence/)
- 앱 쪽 실행은 Spring Data Redis Scripting. → [Scripting](https://docs.spring.io/spring-data-redis/reference/redis/scripting.html)

```conf
maxmemory-policy noeviction
appendonly yes
appendfsync everysec
```

---

## MongoDB

역할: 정책·지갑 등 문서 SoR.

| | |
|--|--|
| 구성 | Replica Set PSS × 3 |
| 노드 | 4 vCPU / 32 GB / 500 GB |
| Write Concern | `w: "majority"` (RS 기본에 맞춤) |

**왜 이렇게 두나**

- 다수 투표 멤버에 내구 커밋된 뒤에야 성공으로 본다. → [Write Concern](https://www.mongodb.com/docs/manual/reference/write-concern/), [Replica set write concern](https://www.mongodb.com/docs/manual/core/replica-set-write-concern/)
- 위 용량 가정 수준의 write면 단일 RS로 충분하다고 본다. 샤딩은 실측 후 판단.
- 인덱스는 쿼리·유니크 제약만. 정의는 [`mongodb/indexes.js`](mongodb/indexes.js).

---

## Kafka

역할: 정책 변경 등 **동기 API 밖** 이벤트(감사·후처리).

| | |
|--|--|
| 구성 | Broker × 3 |
| 노드 | 2 vCPU / 8 GB / 100 GB |

```properties
replication.factor=3
min.insync.replicas=2
acks=all
enable.idempotence=true
compression.type=lz4
```

- `acks=all` + `min.insync.replicas`로 ISR 하한을 둔다. → [Producer `acks`](https://kafka.apache.org/documentation/#producerconfigs_acks), [Broker `min.insync.replicas`](https://kafka.apache.org/documentation/#brokerconfigs_min.insync.replicas)
- 멱등 프로듀서로 재시도 중복을 줄인다. → [Producer `enable.idempotence`](https://kafka.apache.org/documentation/#producerconfigs_enable.idempotence)

---

## 애플리케이션 (Kubernetes)

| 워크로드 | 요청 스펙 | 평상시 Pod | 피크 Pod |
|----------|-----------|----------:|---------:|
| `benefit-api` | 4 CPU / 4 Gi | 2 | 6~12 |
| `benefit-admin-api` | 2 CPU / 2 Gi | 1 | 2 |
| `benefit-consumer` | 2 CPU / 2 Gi | 1 | 2 |
| `benefit-batch` | Job마다 지정 | — | — |

`benefit-batch`는 CronJob/Job마다 **CPU·메모리 request/limit을 유동**으로 잡는다.

---

## 로컬

```bash
docker compose up -d   # mongo:8 · redis:8 · kafka:4.0.0
./gradlew :benefit-api:bootRun
./gradlew :benefit-admin-api:bootRun
```

인덱스 초기화는 `mongodb/indexes.js` (compose init 또는 `mongosh`).

---

## 참고

| 주제 | 문서 |
|------|------|
| Redis eviction | https://redis.io/docs/latest/develop/reference/eviction/ |
| Redis AOF | https://redis.io/docs/latest/operate/oss_and_stack/management/persistence/ |
| Redis 단일 스레드·지연 | https://redis.io/docs/latest/operate/oss_and_stack/management/optimization/latency/ |
| Redis Cluster hash tag | https://redis.io/docs/latest/operate/oss_and_stack/reference/cluster-spec/#hash-tags |
| Spring Data Redis Scripting | https://docs.spring.io/spring-data-redis/reference/redis/scripting.html |
| MongoDB Write Concern | https://www.mongodb.com/docs/manual/reference/write-concern/ |
| Kafka producer acks | https://kafka.apache.org/documentation/#producerconfigs_acks |
| Kafka min.insync.replicas | https://kafka.apache.org/documentation/#brokerconfigs_min.insync.replicas |
| Kafka idempotent producer | https://kafka.apache.org/documentation/#producerconfigs_enable.idempotence |
