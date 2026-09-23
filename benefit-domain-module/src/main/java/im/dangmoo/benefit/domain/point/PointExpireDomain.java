package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointExpireCondition;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class PointExpireDomain {

    private final Instant expiresAt;

    private PointExpireDomain(final Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public static PointExpireDomain of(final PointPolicyDocument policy, final Instant grantedAt) {
        return new PointExpireDomain(expiresAtBy(policy.getExpireCondition(), grantedAt));
    }

    public static PointExpireDomain of(final Instant expiresAt) {
        return new PointExpireDomain(expiresAt);
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant expiresAtOrNull() {
        return neverExpires() ? null : expiresAt;
    }

    public boolean neverExpires() {
        return PointBalanceDocument.isNever(expiresAt);
    }

    public boolean isExpiredAt(final Instant asOf) {
        return !neverExpires() && !expiresAt.isAfter(asOf);
    }

    private static Instant expiresAtBy(
        final PointExpireCondition expireCondition,
        final Instant grantedAt
    ) {
        return switch (expireCondition.getType()) {
            case NEVER -> PointBalanceDocument.NEVER_EXPIRES_AT;
            case FIXED_AT -> PointBalanceDocument.toExpiresKey(expireCondition.getExpiresAt());
            case DAYS_AFTER_GRANT -> PointBalanceDocument.toExpiresKey(grantedAt)
                .plus(expireCondition.getDaysAfterGrant(), ChronoUnit.DAYS);
        };
    }
}
