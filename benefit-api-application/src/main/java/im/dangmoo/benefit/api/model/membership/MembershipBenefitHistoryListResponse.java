package im.dangmoo.benefit.api.model.membership;

import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitHistoryDocument;

import java.util.List;

public record MembershipBenefitHistoryListResponse(
    List<MembershipBenefitHistoryResponse> items,
    long totalElements,
    int page,
    int size
) {

    public static MembershipBenefitHistoryListResponse of(
        final List<MembershipBenefitHistoryDocument> histories,
        final long totalElements,
        final int page,
        final int size
    ) {
        return new MembershipBenefitHistoryListResponse(
            histories.stream().map(MembershipBenefitHistoryResponse::of).toList(),
            totalElements,
            page,
            size
        );
    }
}
