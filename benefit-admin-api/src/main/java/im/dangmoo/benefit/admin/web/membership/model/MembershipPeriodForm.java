package im.dangmoo.benefit.admin.web.membership.model;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPeriod;

import java.time.Instant;

public record MembershipPeriodForm(
    Integer months,
    Instant start,
    Instant end
) {

    public static MembershipPeriodForm of(final MembershipPeriod period) {
        if (period == null) {
            return null;
        }
        return new MembershipPeriodForm(period.getMonths(), period.getStart(), period.getEnd());
    }

    public MembershipPeriod toEntity() {
        return MembershipPeriod.create(months);
    }
}
