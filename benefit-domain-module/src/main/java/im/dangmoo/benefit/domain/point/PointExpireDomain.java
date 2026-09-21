package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PointExpireDomain {

    public static final Instant NEVER_EXPIRES_AT = PointBalance.NEVER_EXPIRES_AT;

    private final PointExpireType type;
    private final Instant expiresAt;
    private final Integer daysAfterGrant;

    private PointExpireDomain(
        final PointExpireType type,
        final Instant expiresAt,
        final Integer daysAfterGrant
    ) {
        this.type = type;
        this.expiresAt = expiresAt;
        this.daysAfterGrant = daysAfterGrant;
    }

    public static PointExpireDomain of(final PointExpireCondition condition) {
        return new PointExpireDomain(
            condition.getType(),
            condition.getExpiresAt(),
            condition.getDaysAfterGrant()
        );
    }

    public Instant resolveExpiresAt(final Instant grantedAt) {
        return switch (type) {
            case NEVER -> NEVER_EXPIRES_AT;
            case FIXED_AT -> PointBalance.toExpiresKey(expiresAt);
            case DAYS_AFTER_GRANT -> PointBalance.toExpiresKey(grantedAt).plus(daysAfterGrant, ChronoUnit.DAYS);
        };
    }

    public static boolean isNever(final Instant expiresAt) {
        return PointBalance.isNever(expiresAt);
    }

    public static Instant toClientExpiresAt(final Instant expiresAt) {
        return isNever(expiresAt) ? null : expiresAt;
    }
}
