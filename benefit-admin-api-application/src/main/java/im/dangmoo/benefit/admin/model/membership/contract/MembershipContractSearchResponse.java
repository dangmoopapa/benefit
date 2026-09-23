package im.dangmoo.benefit.admin.model.membership.contract;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractDocument;

import java.util.List;

public record MembershipContractSearchResponse(List<MembershipContractResponse> items) {

    public static MembershipContractSearchResponse of(
        final List<MembershipContractDocument> contracts
    ) {
        return new MembershipContractSearchResponse(
            contracts.stream().map(MembershipContractResponse::of).toList()
        );
    }
}
