package im.dangmoo.benefit.admin.model.membership.contract;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContract;

import java.util.List;

public record MembershipContractSearchResponse(List<MembershipContractResponse> items) {

    public static MembershipContractSearchResponse of(
        final List<MembershipContract> contracts
    ) {
        return new MembershipContractSearchResponse(
            contracts.stream().map(MembershipContractResponse::of).toList()
        );
    }
}
