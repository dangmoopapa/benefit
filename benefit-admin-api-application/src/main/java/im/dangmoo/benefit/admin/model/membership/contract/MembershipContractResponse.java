package im.dangmoo.benefit.admin.model.membership.contract;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;
import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractStatus;

import java.time.Instant;

public record MembershipContractResponse(
    String id,
    String userId,
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
            contract.getUserId(),
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
