package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class MembershipContractDomain {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    private final String policyId;
    private final String userId;
    private final Instant periodStart;

    private MembershipContractDomain(
        final String policyId,
        final String userId,
        final Instant periodStart
    ) {
        this.policyId = policyId;
        this.userId = userId;
        this.periodStart = periodStart;
    }

    public static MembershipContractDomain joining(
        final String policyId,
        final String userId,
        final Instant joinedAt
    ) {
        return new MembershipContractDomain(policyId, userId, joinedAt);
    }

    public static MembershipContractDomain renewing(final MembershipContractDocument contract) {
        return new MembershipContractDomain(
            contract.getPolicyId(),
            contract.getUserId(),
            contract.getPeriodEnd()
        );
    }

    public String contractKey() {
        return policyId + ":" + userId;
    }

    public Instant periodEnd() {
        return ZonedDateTime.ofInstant(periodStart, ZONE).plusMonths(1).toInstant();
    }
}
