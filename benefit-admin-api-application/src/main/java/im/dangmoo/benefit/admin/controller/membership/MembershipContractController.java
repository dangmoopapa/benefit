package im.dangmoo.benefit.admin.controller.membership;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractJoinRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractLeaveRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractRenewRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractResponse;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractSearchRequest;
import im.dangmoo.benefit.admin.model.membership.contract.MembershipContractSearchResponse;
import im.dangmoo.benefit.admin.usecase.membership.MembershipContractJoinUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipContractLeaveUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipContractRenewUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipContractSearchUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipContractController {

    private final MembershipContractSearchUseCase membershipContractSearchUseCase;
    private final MembershipContractJoinUseCase membershipContractJoinUseCase;
    private final MembershipContractLeaveUseCase membershipContractLeaveUseCase;
    private final MembershipContractRenewUseCase membershipContractRenewUseCase;

    public MembershipContractController(
        final MembershipContractSearchUseCase membershipContractSearchUseCase,
        final MembershipContractJoinUseCase membershipContractJoinUseCase,
        final MembershipContractLeaveUseCase membershipContractLeaveUseCase,
        final MembershipContractRenewUseCase membershipContractRenewUseCase
    ) {
        this.membershipContractSearchUseCase = membershipContractSearchUseCase;
        this.membershipContractJoinUseCase = membershipContractJoinUseCase;
        this.membershipContractLeaveUseCase = membershipContractLeaveUseCase;
        this.membershipContractRenewUseCase = membershipContractRenewUseCase;
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_CONTRACTS)
    AdminApiResponse<MembershipContractSearchResponse> search(
        @ModelAttribute final MembershipContractSearchRequest request
    ) {
        return AdminApiResponse.of(membershipContractSearchUseCase.execute(request));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_CONTRACT_JOIN)
    AdminApiResponse<MembershipContractResponse> join(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipContractJoinRequest request
    ) {
        return AdminApiResponse.of(membershipContractJoinUseCase.execute(adminId, request));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_CONTRACT_LEAVE)
    AdminApiResponse<MembershipContractResponse> leave(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipContractLeaveRequest request
    ) {
        return AdminApiResponse.of(membershipContractLeaveUseCase.execute(adminId, request));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_CONTRACT_RENEW)
    AdminApiResponse<MembershipContractResponse> renew(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipContractRenewRequest request
    ) {
        return AdminApiResponse.of(membershipContractRenewUseCase.execute(adminId, request));
    }
}
