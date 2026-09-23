package im.dangmoo.benefit.data.infrastructure.point;

import im.dangmoo.benefit.data.infrastructure.RedisRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import im.dangmoo.benefit.data.entity.point.stock.*;

@Repository
public class PointGrantStockRedisRepository extends RedisRepository<PointGrantStockKey> {

    public PointGrantStockRedisRepository(final StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    public long get(final String policyId) {
        return super.getAsLong(PointGrantStockKey.of(policyId));
    }

    public boolean tryReserve(final String policyId, final Long stockQuantity) {
        final PointGrantStockKey key = PointGrantStockKey.of(policyId);
        final long after = super.increment(key);
        if (stockQuantity != null && after > stockQuantity) {
            super.decrement(key);
            return false;
        }
        return true;
    }

    public void release(final String policyId) {
        super.decrement(PointGrantStockKey.of(policyId));
    }
}
