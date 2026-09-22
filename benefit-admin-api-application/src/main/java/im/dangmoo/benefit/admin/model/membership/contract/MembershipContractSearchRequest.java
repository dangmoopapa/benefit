package im.dangmoo.benefit.admin.model.membership.contract;

import im.dangmoo.benefit.infrastructure.data.membership.contract.MembershipContractStatus;

public record MembershipContractSearchRequest(
    String userId,
    String policyId,
    MembershipContractStatus status
) {
}
