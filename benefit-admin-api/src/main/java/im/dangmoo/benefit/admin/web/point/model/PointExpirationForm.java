package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.data.point.policy.PointExpirationCondition;
import im.dangmoo.benefit.domain.data.point.policy.PointExpirationType;

import java.time.Instant;

public record PointExpirationForm(
    PointExpirationType type,
    Integer days,
    Integer hours,
    Instant end
) {

    public PointExpirationCondition toDocument() {
        return PointExpirationCondition.create(type, days, hours, end);
    }

    public static PointExpirationForm of(final PointExpirationCondition document) {
        return new PointExpirationForm(
            document.getType(),
            document.getDays(),
            document.getHours(),
            document.getEnd()
        );
    }
}
