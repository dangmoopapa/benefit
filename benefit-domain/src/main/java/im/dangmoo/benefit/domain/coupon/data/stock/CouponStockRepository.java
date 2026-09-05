package im.dangmoo.benefit.domain.coupon.data.stock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponStockRepository {

    private static final String UNLIMITED_TOTAL = "";
    private static final long UNKNOWN_ISSUED_COUNT = 0L;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<String> reserveScript;

    public CouponStockRepository(
        final StringRedisTemplate redisTemplate,
        final RedisScript<String> couponStockReserveScript
    ) {
        this.redisTemplate = redisTemplate;
        this.reserveScript = couponStockReserveScript;
    }

    /**
     * 총량 → 유저 순. 솔드아웃이면 유저 키를 보지 않는다.
     * 무제한이면 유저 발급 여부만 본다.
     */
    public CouponStockSnapshot inspect(final String policyId, final String userId, final Long totalQuantity) {
        if (totalQuantity != null) {
            final long issuedCount = readIssuedCount(policyId);
            if (issuedCount >= totalQuantity) {
                return CouponStockSnapshot.soldOut(issuedCount, totalQuantity);
            }
            if (hasUserIssued(policyId, userId)) {
                return CouponStockSnapshot.alreadyIssued(issuedCount, totalQuantity);
            }
            return CouponStockSnapshot.available(issuedCount, totalQuantity);
        }

        if (hasUserIssued(policyId, userId)) {
            return CouponStockSnapshot.alreadyIssued(UNKNOWN_ISSUED_COUNT, null);
        }
        return CouponStockSnapshot.available(UNKNOWN_ISSUED_COUNT, null);
    }

    public CouponStockReserveResult reserve(final String policyId, final String userId, final Long totalQuantity) {
        return CouponStockReserveResult.fromReply(
            redisTemplate.execute(
                reserveScript,
                List.of(issuedCountKey(policyId), userMarkerKey(policyId, userId)),
                totalArg(totalQuantity)
            )
        );
    }

    private long readIssuedCount(final String policyId) {
        final String value = redisTemplate.opsForValue().get(issuedCountKey(policyId));
        return value == null ? UNKNOWN_ISSUED_COUNT : Long.parseLong(value);
    }

    private boolean hasUserIssued(final String policyId, final String userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(userMarkerKey(policyId, userId)));
    }

    private static String issuedCountKey(final String policyId) {
        return "c:stock:" + policyId + ":issued";
    }

    private static String userMarkerKey(final String policyId, final String userId) {
        return "c:stock:" + policyId + ":u:" + userId;
    }

    private static String totalArg(final Long totalQuantity) {
        return totalQuantity == null ? UNLIMITED_TOTAL : Long.toString(totalQuantity);
    }
}
