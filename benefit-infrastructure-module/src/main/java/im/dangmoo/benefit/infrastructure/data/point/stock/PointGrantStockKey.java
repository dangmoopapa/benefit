package im.dangmoo.benefit.infrastructure.data.point.stock;

import im.dangmoo.benefit.infrastructure.collection.redis.RedisKey;
import im.dangmoo.benefit.infrastructure.collection.redis.RedisKeyName;

public class PointGrantStockKey extends RedisKey {

    private final String policyId;

    private PointGrantStockKey(final String policyId) {
        super(RedisKeyName.POINT_GRANT_STOCK);
        this.policyId = policyId;
    }

    public static PointGrantStockKey of(final String policyId) {
        return new PointGrantStockKey(policyId);
    }

    public String policyId() {
        return policyId;
    }

    @Override
    protected String suffix() {
        return policyId;
    }
}
