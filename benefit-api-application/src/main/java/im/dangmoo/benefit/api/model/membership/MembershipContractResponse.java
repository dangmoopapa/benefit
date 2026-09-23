package im.dangmoo.benefit.api.model.membership;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractStatus;

import java.time.Instant;

public record MembershipContractResponse(
    String id,
    String policyId,
    String policyKey,
    MembershipSeason season,
    MembershipContractStatus status,
    Instant periodStart,
    Instant periodEnd,
    boolean cancelAtPeriodEnd,
    boolean autoRenew
) {

    public static MembershipContractResponse of(final MembershipContractDocument contract) {
        return new MembershipContractResponse(
            contract.getId(),
            contract.getPolicyId(),
            contract.getPolicyKey(),
            contract.getSeason(),
            contract.getStatus(),
            contract.getPeriodStart(),
            contract.getPeriodEnd(),
            contract.isCancelAtPeriodEnd(),
            contract.isAutoRenew()
        );
    }
}
