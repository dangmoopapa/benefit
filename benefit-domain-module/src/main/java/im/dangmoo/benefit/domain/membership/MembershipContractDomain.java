package im.dangmoo.benefit.domain.membership;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class MembershipContractDomain {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private MembershipContractDomain() {
    }

    public static String idempotencyKey(final String policyId, final String userId) {
        return policyId + ":" + userId;
    }

    public static Instant nextPeriodEnd(final Instant from) {
        return ZonedDateTime.ofInstant(from, ZONE).plusMonths(1).toInstant();
    }
}
