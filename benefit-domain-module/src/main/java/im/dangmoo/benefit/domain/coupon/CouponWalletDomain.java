package im.dangmoo.benefit.domain.coupon;

public final class CouponWalletDomain {

    private CouponWalletDomain() {
    }

    public static String idempotencyKey(final String policyId, final String userId) {
        return policyId + ":" + userId;
    }
}
