package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContract;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractStatus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class MembershipContractDomain {

    public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private MembershipContractDomain() {
    }

    public static String idempotencyKey(final String policyId, final String userId) {
        return policyId + ":" + userId;
    }

    public static boolean isEffective(final MembershipContract contract, final Instant now) {
        if (contract == null) {
            return false;
        }
        if (contract.getStatus() != MembershipContractStatus.ACTIVE) {
            return false;
        }
        return contract.getPeriodEnd().isAfter(now);
    }

    public static Instant nextPeriodEnd(final Instant from) {
        return ZonedDateTime.ofInstant(from, ZONE).plusMonths(1).toInstant();
    }
}
