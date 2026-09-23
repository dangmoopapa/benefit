package im.dangmoo.benefit.admin.dto.membership.contract;

import im.dangmoo.benefit.data.entity.membership.contract.MembershipContractStatus;

public record MembershipContractSearchRequest(
    String userId,
    String policyId,
    MembershipContractStatus status
) {
}
