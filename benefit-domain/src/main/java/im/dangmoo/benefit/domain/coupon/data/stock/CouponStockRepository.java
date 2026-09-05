package im.dangmoo.benefit.domain.coupon.data.stock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponStockRepository {

    private final StringRedisTemplate redis;
    private final RedisScript<String> inspectScript;
    private final RedisScript<String> reserveScript;
    private final RedisScript<Long> recallScript;

    public CouponStockRepository(
        final StringRedisTemplate redis,
        final RedisScript<String> couponStockInspectScript,
        final RedisScript<String> couponStockReserveScript,
        final RedisScript<Long> couponStockRecallScript
    ) {
        this.redis = redis;
        this.inspectScript = couponStockInspectScript;
        this.reserveScript = couponStockReserveScript;
        this.recallScript = couponStockRecallScript;
    }

    public CouponStockResult inspect(final String policyId, final String userId, final Long totalQuantity) {
        final String reply = redis.execute(
            inspectScript,
            keys(policyId, userId),
            totalArg(totalQuantity)
        );
        return CouponStockResult.inspected(reply, totalQuantity);
    }

    public CouponStockResult reserve(final String policyId, final String userId, final Long totalQuantity) {
        final String reply = redis.execute(
            reserveScript,
            keys(policyId, userId),
            totalArg(totalQuantity)
        );
        return CouponStockResult.reserved(reply, totalQuantity);
    }

    public void recall(final String policyId, final String userId) {
        redis.execute(recallScript, keys(policyId, userId));
    }

    private static List<String> keys(final String policyId, final String userId) {
        return List.of(
            "c:stock:" + policyId + ":issued",
            "c:stock:" + policyId + ":u:" + userId
        );
    }

    private static String totalArg(final Long totalQuantity) {
        return totalQuantity == null ? "" : totalQuantity.toString();
    }
}
