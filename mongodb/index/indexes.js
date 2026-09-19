const dbName = 'benefit';
db = db.getSiblingDB(dbName);

db.coupon_policies.createIndex(
  { key: 1 },
  { unique: true, name: 'uk_key' }
);

db.coupon_policies.createIndex(
  { type: 1, status: 1, 'applyCondition.productIds': 1 },
  { name: 'ix_type_status_productIds' }
);

db.coupon_policies.createIndex(
  { type: 1, status: 1, 'applyCondition.brandIds': 1 },
  { name: 'ix_type_status_brandIds' }
);

db.coupon_wallets.createIndex(
  { idempotencyKey: 1 },
  { unique: true, name: 'uk_idempotencyKey' }
);

db.coupon_wallets.createIndex(
  { userId: 1, status: 1 },
  { name: 'ix_userId_status' }
);

db.coupon_wallets.createIndex(
  { policyId: 1 },
  { name: 'ix_policyId' }
);

db.coupon_codes.createIndex(
  { code: 1 },
  { unique: true, name: 'uk_code' }
);

db.coupon_codes.createIndex(
  { policyId: 1 },
  { name: 'ix_policyId' }
);
