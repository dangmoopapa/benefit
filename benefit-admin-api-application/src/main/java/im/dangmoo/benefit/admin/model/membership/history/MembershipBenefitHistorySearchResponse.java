package im.dangmoo.benefit.admin.model.membership.history;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryDocument;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryStatus;
import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;

import java.time.Instant;
import java.util.List;

public record MembershipBenefitHistorySearchResponse(
    List<Item> items,
    long totalElements,
    int page,
    int size
) {

    public static MembershipBenefitHistorySearchResponse of(
        final List<MembershipBenefitHistoryDocument> histories,
        final long totalElements,
        final int page,
        final int size
    ) {
        return new MembershipBenefitHistorySearchResponse(
            histories.stream().map(Item::of).toList(),
            totalElements,
            page,
            size
        );
    }

    public record Item(
        String id,
        String userId,
        String orderId,
        String contractId,
        String policyId,
        MembershipSeason season,
        MembershipBenefitApplied applied,
        MembershipBenefitHistoryStatus status,
        Instant transactionAt
    ) {
        public static Item of(final MembershipBenefitHistoryDocument history) {
            return new Item(
                history.getId(),
                history.getUserId(),
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
}
