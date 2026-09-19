package im.dangmoo.benefit.infrastructure.data.coupon.stock;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponTimeAttackIssueScript {

    private static final String UNLIMITED_TOTAL = "";
    private static final String SCRIPT_PATH = "redis/coupon_time_attack_issue.lua";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<String> script;

    public CouponTimeAttackIssueScript(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.script = RedisScript.of(new ClassPathResource(SCRIPT_PATH), String.class);
    }

    public CouponTimeAttackIssueResult execute(
        final String policyId,
        final String idempotencyKey,
        final Long stockQuantity
    ) {
        return CouponTimeAttackIssueResult.fromReply(
            redisTemplate.execute(
                script,
                List.of(
                    CouponIssueStockKey.of(policyId).getKey(),
                    CouponIssueMarkerKey.of(idempotencyKey).getKey()
                ),
                stockQuantity == null ? UNLIMITED_TOTAL : Long.toString(stockQuantity)
            )
        );
    }

    public boolean hasIssued(final String idempotencyKey) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(CouponIssueMarkerKey.of(idempotencyKey).getKey())
        );
    }
}
