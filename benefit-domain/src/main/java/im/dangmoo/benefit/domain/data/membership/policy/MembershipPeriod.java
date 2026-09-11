package im.dangmoo.benefit.domain.data.membership.policy;

import im.dangmoo.benefit.domain.util.TimeUtils;

import java.time.Instant;

public class MembershipPeriod {

    private Integer months;
    private Instant start;
    private Instant end;

    private MembershipPeriod() {
    }

    public static MembershipPeriod create(final Integer months) {
        final MembershipPeriod entity = new MembershipPeriod();
        entity.months = months;
        return entity;
    }

    public MembershipPeriod open(final Instant startedAt) {
        final MembershipPeriod entity = new MembershipPeriod();
        entity.months = months;
        entity.start = startedAt;
        entity.end = resolveExpiresAt(startedAt);
        return entity;
    }

    public void close(final Instant endedAt) {
        this.end = endedAt;
    }

    public boolean contains(final Instant now) {
        if (start != null && now.isBefore(start)) {
            return false;
        }
        return end == null || !now.isAfter(end);
    }

    public Instant resolveExpiresAt(final Instant startedAt) {
        if (months == null || months <= 0) {
            return startedAt;
        }
        return TimeUtils.plusUtcMonths(startedAt, months);
    }

    public Integer getMonths() {
        return months;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }
}
