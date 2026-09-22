package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueFrequency;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.YearMonth;

public final class CouponWalletDomain {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private CouponWalletDomain() {
    }

    public static String idempotencyKey(
        final String policyId,
        final String userId,
        final CouponIssueFrequency frequency,
        final Instant at
    ) {
        final CouponIssueFrequency resolved =
            frequency == null ? CouponIssueFrequency.ONCE_PER_USER : frequency;
        final String prefix = policyId + ":" + userId;
        return switch (resolved) {
            case ONCE_PER_USER -> prefix;
            case ONCE_PER_DAY -> prefix + ":" + LocalDate.from(at.atZone(ZONE));
            case ONCE_PER_MONTH -> prefix + ":" + YearMonth.from(at.atZone(ZONE));
            case ONCE_PER_YEAR -> prefix + ":" + at.atZone(ZONE).getYear();
        };
    }
}
