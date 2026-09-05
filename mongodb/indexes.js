// mongosh < mongodb/indexes.js
// docker init: /docker-entrypoint-initdb.d 에서도 동일 파일 사용
//
// 원칙
// - 실제 쿼리·유니크 제약만 둔다. 좌측 prefix로 커버되면 단일 필드 인덱스는 안 만든다.
// - contains(name) 검색은 B-tree로 못 탄다 → name 인덱스 없음.
// - (userId, policyId) unique = Redis 마커와 같은 1인 1장 안전망. N장 허용 시 이 unique를 바꿔야 한다.

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

// 구버전 인덱스 정리 (있으면)
dropIndexQuietly(db.coupon_policies, 'ix_platformId_status');

// --- coupon_wallets --------------------------------------------------------

// 발급 레이스·중복 insert 안전망. prefix로 userId 전체 목록(쿠폰함)도 커버.
db.coupon_wallets.createIndex(
  { userId: 1, policyId: 1 },
  { unique: true, name: 'uk_userId_policyId' }
);

// admin: 정책별 발급 목록
db.coupon_wallets.createIndex(
  { policyId: 1, issuedAt: -1 },
  { name: 'ix_policyId_issuedAt' }
);

// 쿠폰함 필터·만료 배치 후보: userId + status + expiresAt
// (현재 앱은 userId만 조회 후 메모리 필터. 곧 DB 필터로 내릴 때 사용)
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

// 구버전: uk_userId_policyId prefix로 충분 → 중복 write 증폭만 남김
dropIndexQuietly(db.coupon_wallets, 'ix_userId');
dropIndexQuietly(db.coupon_wallets, 'ix_policyId');

print('indexes ok @ ' + dbName);
printjson(db.coupon_policies.getIndexes());
printjson(db.coupon_wallets.getIndexes());

function dropIndexQuietly(coll, name) {
  try {
    coll.dropIndex(name);
    print('dropped ' + coll.getName() + '.' + name);
  } catch (e) {
    // ignore missing
  }
}
