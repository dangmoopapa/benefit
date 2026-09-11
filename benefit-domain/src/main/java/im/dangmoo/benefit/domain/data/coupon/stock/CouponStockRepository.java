package im.dangmoo.benefit.domain.data.coupon.stock;

import im.dangmoo.benefit.domain.infrastructure.redis.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponStockRepository {

    private static final String UNLIMITED_TOTAL = "";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<String> reserveScript;

    public CouponStockRepository(
        final StringRedisTemplate redisTemplate,
        final RedisScript<String> couponStockReserveScript
    ) {
        this.redisTemplate = redisTemplate;
        this.reserveScript = couponStockReserveScript;
    }

    public long getIssuedCount(final String policyId) {
        final String value = redisTemplate.opsForValue().get(RedisKeys.couponStockIssued(policyId));
        return value == null ? 0L : Long.parseLong(value);
    }

    public boolean hasIssued(final String policyId, final String idempotencyKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(RedisKeys.couponStockUser(policyId, idempotencyKey)));
    }

    public CouponStockReserveResult reserve(
        final String policyId,
        final String idempotencyKey,
        final Long totalQuantity
    ) {
        return CouponStockReserveResult.fromReply(
            redisTemplate.execute(
                reserveScript,
                List.of(
                    RedisKeys.couponStockIssued(policyId),
                    RedisKeys.couponStockUser(policyId, idempotencyKey)
                ),
                totalQuantity == null ? UNLIMITED_TOTAL : Long.toString(totalQuantity)
            )
        );
    }
}
