package im.dangmoo.benefit.domain.coupon;

import java.time.Instant;
import java.time.ZoneId;
import java.time.YearMonth;

public final class CouponWalletDomain {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private CouponWalletDomain() {
    }

    public static String idempotencyKey(final String policyId, final String userId) {
        return policyId + ":" + userId;
    }

    public static String monthlyIdempotencyKey(
        final String policyId,
        final String userId,
        final Instant at
    ) {
        final YearMonth yearMonth = YearMonth.from(at.atZone(ZONE));
        return policyId + ":" + userId + ":" + yearMonth;
    }
}
