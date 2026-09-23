package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import im.dangmoo.benefit.infrastructure.support.redis.RedisKey;
import im.dangmoo.benefit.infrastructure.support.redis.RedisKeyName;

public class CouponIssueMarkerKey extends RedisKey {

    private final String idempotencyKey;

    private CouponIssueMarkerKey(final String idempotencyKey) {
        super(RedisKeyName.COUPON_ISSUE_MARKER);
        this.idempotencyKey = idempotencyKey;
    }

    public static CouponIssueMarkerKey of(final String idempotencyKey) {
        return new CouponIssueMarkerKey(idempotencyKey);
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    @Override
    protected String suffix() {
        return idempotencyKey;
    }
}
