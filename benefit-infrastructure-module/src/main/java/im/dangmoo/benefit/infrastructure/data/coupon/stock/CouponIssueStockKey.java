package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import im.dangmoo.benefit.infrastructure.collection.redis.RedisKey;
import im.dangmoo.benefit.infrastructure.collection.redis.RedisKeyName;

public class CouponIssueStockKey extends RedisKey {

    private final String policyId;

    private CouponIssueStockKey(final String policyId) {
        super(RedisKeyName.COUPON_ISSUE_STOCK);
        this.policyId = policyId;
    }

    public static CouponIssueStockKey of(final String policyId) {
        return new CouponIssueStockKey(policyId);
    }

    public String policyId() {
        return policyId;
    }

    @Override
    protected String suffix() {
        return policyId;
    }
}
