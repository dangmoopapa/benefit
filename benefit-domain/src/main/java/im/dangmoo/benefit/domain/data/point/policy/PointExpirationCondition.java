package im.dangmoo.benefit.domain.data.point.policy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PointExpirationCondition {

    private PointExpirationType type;
    private Integer days;
    private Integer hours;
    private Instant end;

    private PointExpirationCondition() {
    }

    public static PointExpirationCondition create(
        final PointExpirationType type,
        final Integer days,
        final Integer hours,
        final Instant end
    ) {
        final PointExpirationCondition document = new PointExpirationCondition();
        document.type = type;
        document.days = days;
        document.hours = hours;
        document.end = end;
        return document;
    }

    public Instant resolveExpiresAt(final Instant issuedAt) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case NONE -> null;
            case FIXED_END -> end;
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
        };
    }

    public PointExpirationType getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public Integer getHours() {
        return hours;
    }

    public Instant getEnd() {
        return end;
    }
}
