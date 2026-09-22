package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointIssueFrequency;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.YearMonth;

public final class PointTransactionDomain {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private PointTransactionDomain() {
    }

    public static String grantKey(
        final String policyId,
        final String userId,
        final PointIssueFrequency frequency,
        final Instant at
    ) {
        final PointIssueFrequency resolved =
            frequency == null ? PointIssueFrequency.ONCE_PER_USER : frequency;
        final String prefix = PointTransactionType.GRANT.name() + ":" + policyId + ":" + userId;
        return switch (resolved) {
            case ONCE_PER_USER -> prefix;
            case ONCE_PER_DAY -> prefix + ":" + LocalDate.from(at.atZone(ZONE));
            case ONCE_PER_MONTH -> prefix + ":" + YearMonth.from(at.atZone(ZONE));
            case ONCE_PER_YEAR -> prefix + ":" + at.atZone(ZONE).getYear();
        };
    }

    public static String useKey(final String orderId) {
        return PointTransactionType.USE.name() + ":" + orderId;
    }
}
