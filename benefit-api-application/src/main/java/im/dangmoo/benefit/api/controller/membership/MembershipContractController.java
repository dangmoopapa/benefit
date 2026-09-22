package im.dangmoo.benefit.api.controller.membership;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.membership.MembershipContractJoinRequest;
import im.dangmoo.benefit.api.model.membership.MembershipContractResponse;
import im.dangmoo.benefit.api.usecase.membership.MembershipContractDetailUseCase;
import im.dangmoo.benefit.api.usecase.membership.MembershipContractJoinUseCase;
import im.dangmoo.benefit.api.usecase.membership.MembershipContractLeaveUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipContractController {

    private final MembershipContractDetailUseCase membershipContractDetailUseCase;
    private final MembershipContractJoinUseCase membershipContractJoinUseCase;
    private final MembershipContractLeaveUseCase membershipContractLeaveUseCase;

    public MembershipContractController(
        final MembershipContractDetailUseCase membershipContractDetailUseCase,
        final MembershipContractJoinUseCase membershipContractJoinUseCase,
        final MembershipContractLeaveUseCase membershipContractLeaveUseCase
    ) {
        this.membershipContractDetailUseCase = membershipContractDetailUseCase;
        this.membershipContractJoinUseCase = membershipContractJoinUseCase;
        this.membershipContractLeaveUseCase = membershipContractLeaveUseCase;
    }

    @GetMapping(ApiPath.MEMBERSHIP_CONTRACTS)
    ApiResponse<MembershipContractResponse> me(
        @RequestHeader(ApiHeaders.USER_ID) final String userId
    ) {
        return ApiResponse.of(membershipContractDetailUseCase.detail(userId));
    }

    @PostMapping(ApiPath.MEMBERSHIP_CONTRACT_JOIN)
    ApiResponse<MembershipContractResponse> join(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final MembershipContractJoinRequest request
    ) {
        return ApiResponse.of(membershipContractJoinUseCase.join(userId, request));
    }

    @PostMapping(ApiPath.MEMBERSHIP_CONTRACT_LEAVE)
    ApiResponse<MembershipContractResponse> leave(
        @RequestHeader(ApiHeaders.USER_ID) final String userId
    ) {
        return ApiResponse.of(membershipContractLeaveUseCase.leave(userId));
    }
}
