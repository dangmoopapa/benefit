package im.dangmoo.benefit.data.entity.point.policy.condition;

import java.time.Instant;

public class PointExpireCondition {

    private PointExpireType type;
    private Instant expiresAt;
    private Integer daysAfterGrant;

    private PointExpireCondition() {
    }

    public static PointExpireCondition create(
        final PointExpireType type,
        final Instant expiresAt,
        final Integer daysAfterGrant
    ) {
        final PointExpireCondition condition = new PointExpireCondition();
        condition.type = type;
        condition.expiresAt = expiresAt;
        condition.daysAfterGrant = daysAfterGrant;
        return condition;
    }

    public PointExpireType getType() {
        return type;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Integer getDaysAfterGrant() {
        return daysAfterGrant;
    }
}
