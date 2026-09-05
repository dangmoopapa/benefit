package im.dangmoo.benefit.domain.data.coupon.stock;

import im.dangmoo.benefit.domain.infrastructure.redis.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponUsageRepository {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> consumeScript;
    private final RedisScript<Long> releaseScript;

    public CouponUsageRepository(
        final StringRedisTemplate redisTemplate,
        final RedisScript<Long> couponUsageConsumeScript,
        final RedisScript<Long> couponUsageReleaseScript
    ) {
        this.redisTemplate = redisTemplate;
        this.consumeScript = couponUsageConsumeScript;
        this.releaseScript = couponUsageReleaseScript;
    }

    /**
     * @param totalLimit null 이면 무제한(카운트도 안 함)
     * @return 사용 가능하면 true
     */
    public boolean tryConsume(final String policyId, final Long totalLimit) {
        if (totalLimit == null) {
            return true;
        }
        final Long ok = redisTemplate.execute(
            consumeScript,
            List.of(RedisKeys.couponUsageTotal(policyId)),
            Long.toString(totalLimit)
        );
        return ok != null && ok == 1L;
    }

    public void release(final String policyId) {
        redisTemplate.execute(
            releaseScript,
            List.of(RedisKeys.couponUsageTotal(policyId))
        );
    }
}
