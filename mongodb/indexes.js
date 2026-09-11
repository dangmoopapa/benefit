// mongosh < mongodb/indexes.js
// docker init: /docker-entrypoint-initdb.d 에서도 동일 파일 사용
//
// 원칙
// - 실제 쿼리·유니크 제약만 둔다. 좌측 prefix로 커버되면 단일 필드 인덱스는 안 만든다.
// - contains(name) 검색은 B-tree로 못 탄다 → name 인덱스 없음.
// - 쿠폰 발급 반복은 wallet.idempotencyKey unique. 총량·동시성은 Redis 마커.

const dbName = 'benefit';
db = db.getSiblingDB(dbName);

// --- coupon_policies -------------------------------------------------------

// queryByCode / 정책키 조회
db.coupon_policies.createIndex(
  { code: 1 },
  { unique: true, name: 'uk_code' }
);

// admin list: platformId + status (+ type 선택)
// prefix: platformId / platformId+status
db.coupon_policies.createIndex(
  { platformId: 1, status: 1, type: 1 },
  { name: 'ix_platformId_status_type' }
);

dropIndexQuietly(db.coupon_policies, 'ix_platformId_status');

// --- coupon_wallets --------------------------------------------------------

// 공개 발급 1인 1장·반복 지급은 Redis 마커 + wallet.idempotencyKey.
// MONTHLY 등은 키가 달라서 같은 정책 재지급이 가능하다.
db.coupon_wallets.createIndex(
  { idempotencyKey: 1 },
  { unique: true, sparse: true, name: 'uk_idempotencyKey' }
);

db.coupon_wallets.createIndex(
  { userId: 1, policyId: 1, issuedAt: -1 },
  { name: 'ix_userId_policyId_issuedAt' }
);

dropIndexQuietly(db.coupon_wallets, 'uk_userId_policyId');

// admin: 정책별 발급 목록 (issuedAt desc 정렬 가정)
db.coupon_wallets.createIndex(
  { policyId: 1, issuedAt: -1 },
  { name: 'ix_policyId_issuedAt' }
);

// 쿠폰함 상태·만료 필터 (현재는 userId만 조회 후 메모리 필터. DB 필터 내릴 때 사용)
db.coupon_wallets.createIndex(
  { userId: 1, status: 1, expiresAt: 1 },
  { name: 'ix_userId_status_expiresAt' }
);

// 전역 만료 스윕: status=AVAILABLE && expiresAt < now
db.coupon_wallets.createIndex(
  { expiresAt: 1 },
  {
    name: 'ix_expiresAt_available',
    partialFilterExpression: { status: 'AVAILABLE' }
  }
);

dropIndexQuietly(db.coupon_wallets, 'ix_userId');
dropIndexQuietly(db.coupon_wallets, 'ix_policyId');

// --- membership_policies ---------------------------------------------------

db.membership_policies.createIndex(
  { version: 1 },
  { unique: true, name: 'uk_version' }
);

dropIndexQuietly(db.membership_policies, 'ix_status_version');

// --- memberships -----------------------------------------------------------

db.membership_subscriptions.createIndex(
  { userId: 1 },
  { unique: true, name: 'uk_userId' }
);

dropIndexQuietly(db.memberships, 'uk_userId');
dropIndexQuietly(db.memberships, 'ix_userId_status_startedAt');
dropIndexQuietly(db.membership_grants, 'uk_userId_grantKey_periodKey');

// --- point_policies --------------------------------------------------------

db.point_policies.createIndex(
  { code: 1 },
  { unique: true, name: 'uk_code' }
);

db.point_policies.createIndex(
  { platformId: 1, status: 1 },
  { name: 'ix_platformId_status' }
);

dropIndexQuietly(db.point_policies, 'ix_platformId_status_type');

// --- point_balances --------------------------------------------------------

db.point_balances.createIndex(
  { userId: 1 },
  { unique: true, name: 'uk_userId' }
);

// --- point_transactions ----------------------------------------------------

// 멱등 키
db.point_transactions.createIndex(
  { idempotencyKey: 1 },
  { unique: true, name: 'uk_idempotencyKey' }
);

// 거래내역: userId + transactionAt desc
dropIndexQuietly(db.point_transactions, 'ix_userId_transactionAt');
db.point_transactions.createIndex(
  { userId: 1, transactionAt: -1 },
  { name: 'ix_userId_transactionAt' }
);

// revoke/restore 중복 방지 (relatedTransactionId 있는 문서만)
db.point_transactions.createIndex(
  { relatedTransactionId: 1 },
  { unique: true, sparse: true, name: 'uk_relatedTransactionId' }
);

// 구버전: policyId 조합 쿼리 없음 / type 단독 정렬 인덱스도 불필요
dropIndexQuietly(db.point_transactions, 'ix_userId_type_policyId');
dropIndexQuietly(db.point_transactions, 'ix_userId_type_transactionAt');
dropIndexQuietly(db.point_transactions, 'ix_userId_transactionDate');
dropIndexQuietly(db.point_transactions, 'uk_detail_relatedTransactionId');
dropIndexQuietly(db.point_transactions, 'uk_usedTransactionId');

// 레거시 컬렉션 인덱스 정리
dropIndexQuietly(db.point_accounts, 'uk_userId');
dropIndexQuietly(db.point_accounts, 'uk_earns_idempotencyKeys');
dropIndexQuietly(db.point_accounts, 'uk_earns_idempotencyKey');
dropIndexQuietly(db.point_histories, 'uk_idempotencyKey');
dropIndexQuietly(db.point_histories, 'ix_userId_createdAt');
dropIndexQuietly(db.point_histories, 'ix_relatedHistoryId_type');
dropIndexQuietly(db.point_histories, 'ix_userId_type_policyId');
dropIndexQuietly(db.point_earns, 'uk_idempotencyKey');
dropIndexQuietly(db.point_earns, 'ix_user_usable');
dropIndexQuietly(db.point_earns, 'ix_userId_policyId');

print('indexes ok @ ' + dbName);
printjson(db.coupon_policies.getIndexes());
printjson(db.coupon_wallets.getIndexes());
printjson(db.membership_policies.getIndexes());
printjson(db.membership_subscriptions.getIndexes());
printjson(db.point_policies.getIndexes());
printjson(db.point_balances.getIndexes());
printjson(db.point_transactions.getIndexes());

function dropIndexQuietly(coll, name) {
  try {
    coll.dropIndex(name);
    print('dropped ' + coll.getName() + '.' + name);
  } catch (e) {
    // ignore missing
  }
}
