// mongosh benefit < mongodb/indexes.js

db.coupon_policies.createIndex({ code: 1 }, { unique: true, name: "uk_code" });
db.coupon_policies.createIndex({ platformId: 1, status: 1 }, { name: "ix_platformId_status" });

db.coupon_wallets.createIndex({ userId: 1, policyId: 1 }, { unique: true, name: "uk_userId_policyId" });
db.coupon_wallets.createIndex({ policyId: 1 }, { name: "ix_policyId" });
db.coupon_wallets.createIndex({ userId: 1 }, { name: "ix_userId" });
