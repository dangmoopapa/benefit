package im.dangmoo.benefit.domain.coupon.policy.usage;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class CouponUsageValidity {

    private CouponUsageValidityType type;
    private Instant start;
    private Instant end;
    private Integer days;
    private Integer hours;

    private CouponUsageValidity() {
    }

    public static CouponUsageValidity create(
        final CouponUsageValidityType type,
        final Instant start,
        final Instant end,
        final Integer days,
        final Integer hours
    ) {
        final CouponUsageValidity document = new CouponUsageValidity();
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

    public boolean isActiveAt(final Instant issuedAt, final Instant now) {
        if (type == CouponUsageValidityType.FIXED_PERIOD && start != null && now.isBefore(start)) {
            return false;
        }
        final Instant expiresAt = resolveExpiresAt(issuedAt);
        return expiresAt == null || !now.isAfter(expiresAt);
    }

    public CouponUsageValidityType getType() {
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
