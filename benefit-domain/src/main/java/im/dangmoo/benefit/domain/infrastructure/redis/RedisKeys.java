package im.dangmoo.benefit.domain.infrastructure.redis;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String couponStockIssued(final String policyId) {
        return "c:stock:" + policyId + ":issued";
    }

    public static String couponStockUser(final String policyId, final String userId) {
        return "c:stock:" + policyId + ":u:" + userId;
    }

    public static String couponUsageTotal(final String policyId) {
        return "c:usage:" + policyId + ":total";
    }
}
