package im.dangmoo.benefit.data.infrastructure.coupon;

import im.dangmoo.benefit.data.infrastructure.RedisRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import im.dangmoo.benefit.data.entity.coupon.stock.*;

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
