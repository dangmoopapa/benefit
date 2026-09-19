package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import im.dangmoo.benefit.infrastructure.collection.redis.RedisRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CouponUsageStockRedisRepository extends RedisRepository<CouponUsageStockKey> {

    public CouponUsageStockRedisRepository(final StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    public long get(final String policyId) {
        return super.getAsLong(CouponUsageStockKey.of(policyId));
    }

    public Map<String, Long> get(final List<String> policyIds) {
        return super.multiGetAsLong(
            policyIds.stream().map(CouponUsageStockKey::of).toList(),
            CouponUsageStockKey::policyId
        );
    }

    public void increment(final String policyId) {
        super.increment(CouponUsageStockKey.of(policyId));
    }

    public void decrement(final String policyId) {
        super.decrement(CouponUsageStockKey.of(policyId));
    }
}
