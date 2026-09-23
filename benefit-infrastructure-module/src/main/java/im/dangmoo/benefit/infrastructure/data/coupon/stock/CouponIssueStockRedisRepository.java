package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import im.dangmoo.benefit.infrastructure.support.redis.RedisRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CouponIssueStockRedisRepository extends RedisRepository<CouponIssueStockKey> {

    public CouponIssueStockRedisRepository(final StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    public long get(final String policyId) {
        return super.getAsLong(CouponIssueStockKey.of(policyId));
    }

    public Map<String, Long> get(final List<String> policyIds) {
        return super.multiGetAsLong(
            policyIds.stream().map(CouponIssueStockKey::of).toList(),
            CouponIssueStockKey::policyId
        );
    }

    public long increment(final String policyId) {
        return super.increment(CouponIssueStockKey.of(policyId));
    }
}
