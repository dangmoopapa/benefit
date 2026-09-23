package im.dangmoo.benefit.api.model.membership;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryDocument;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryStatus;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;

import java.time.Instant;

public record MembershipBenefitHistoryResponse(
    String id,
    String orderId,
    String contractId,
    String policyId,
    MembershipSeason season,
    MembershipBenefitApplied applied,
    MembershipBenefitHistoryStatus status,
    Instant transactionAt
) {

    public static MembershipBenefitHistoryResponse of(final MembershipBenefitHistoryDocument history) {
        return new MembershipBenefitHistoryResponse(
            history.getId(),
            history.getOrderId(),
            history.getContractId(),
            history.getPolicyId(),
            history.getSeason(),
            history.getApplied(),
            history.getStatus(),
            history.getTransactionAt()
        );
    }
}
