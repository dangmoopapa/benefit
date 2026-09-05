package im.dangmoo.benefit.domain.infrastructure.redis;

public final class RedisKeys {

    public static String couponStockIssued(final String policyId) {
        return "c:stock:" + policyId + ":issued";
    }

    public static String couponStockUser(final String policyId, final String userId) {
        return "c:stock:" + policyId + ":u:" + userId;
    }
}
