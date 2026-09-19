package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import im.dangmoo.benefit.infrastructure.collection.redis.RedisKey;
import im.dangmoo.benefit.infrastructure.collection.redis.RedisKeyName;

public class CouponUsageStockKey extends RedisKey {

    private final String policyId;

    private CouponUsageStockKey(final String policyId) {
        super(RedisKeyName.COUPON_USAGE_STOCK);
        this.policyId = policyId;
    }

    public static CouponUsageStockKey of(final String policyId) {
        return new CouponUsageStockKey(policyId);
    }

    public String policyId() {
        return policyId;
    }

    @Override
    protected String suffix() {
        return policyId;
    }
}
