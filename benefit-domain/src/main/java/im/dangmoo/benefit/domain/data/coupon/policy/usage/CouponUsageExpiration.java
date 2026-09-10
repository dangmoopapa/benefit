package im.dangmoo.benefit.domain.data.coupon.policy.usage;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class CouponUsageExpiration {

    private CouponUsageExpirationType type;
    private Instant start;
    private Instant end;
    private Integer days;
    private Integer hours;

    private CouponUsageExpiration() {
    }

    public static CouponUsageExpiration create(
        final CouponUsageExpirationType type,
        final Instant start,
        final Instant end,
        final Integer days,
        final Integer hours
    ) {
        final CouponUsageExpiration document = new CouponUsageExpiration();
        document.type = type;
        document.start = start;
        document.end = end;
        document.days = days;
        document.hours = hours;
        return document;
    }

    public Instant resolveExpiresAt(final Instant issuedAt) {
        return switch (type) {
            case FIXED_PERIOD -> end;
            case UNTIL_MIDNIGHT -> issuedAt.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .plusDays(days)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
            case DURATION -> {
                Instant expiresAt = issuedAt;
                if (days != null) {
                    expiresAt = expiresAt.plus(days, ChronoUnit.DAYS);
                }
                if (hours != null) {
                    expiresAt = expiresAt.plus(hours, ChronoUnit.HOURS);
                }
                yield expiresAt;
            }
            case AFTER_PURCHASE -> null;
        };
    }

    public CouponUsageExpirationType getType() {
        return type;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Integer getDays() {
        return days;
    }

    public Integer getHours() {
        return hours;
    }
}
