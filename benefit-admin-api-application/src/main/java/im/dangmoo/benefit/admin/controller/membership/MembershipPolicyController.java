package im.dangmoo.benefit.admin.controller.membership;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyCreateResponse;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyDetailResponse;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicySearchRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicySearchResponse;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.membership.policy.MembershipPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.membership.MembershipPolicyChangeStatusUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipPolicyCreateUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipPolicyDetailUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipPolicySearchUseCase;
import im.dangmoo.benefit.admin.usecase.membership.MembershipPolicyUpdateUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipPolicyController {

    private final MembershipPolicySearchUseCase membershipPolicySearchUseCase;
    private final MembershipPolicyDetailUseCase membershipPolicyDetailUseCase;
    private final MembershipPolicyCreateUseCase membershipPolicyCreateUseCase;
    private final MembershipPolicyUpdateUseCase membershipPolicyUpdateUseCase;
    private final MembershipPolicyChangeStatusUseCase membershipPolicyChangeStatusUseCase;

    public MembershipPolicyController(
        final MembershipPolicySearchUseCase membershipPolicySearchUseCase,
        final MembershipPolicyDetailUseCase membershipPolicyDetailUseCase,
        final MembershipPolicyCreateUseCase membershipPolicyCreateUseCase,
        final MembershipPolicyUpdateUseCase membershipPolicyUpdateUseCase,
        final MembershipPolicyChangeStatusUseCase membershipPolicyChangeStatusUseCase
    ) {
        this.membershipPolicySearchUseCase = membershipPolicySearchUseCase;
        this.membershipPolicyDetailUseCase = membershipPolicyDetailUseCase;
        this.membershipPolicyCreateUseCase = membershipPolicyCreateUseCase;
        this.membershipPolicyUpdateUseCase = membershipPolicyUpdateUseCase;
        this.membershipPolicyChangeStatusUseCase = membershipPolicyChangeStatusUseCase;
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_POLICIES)
    AdminApiResponse<MembershipPolicySearchResponse> search(
        @ModelAttribute final MembershipPolicySearchRequest request
    ) {
        return AdminApiResponse.of(membershipPolicySearchUseCase.execute(request));
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_POLICY)
    AdminApiResponse<MembershipPolicyDetailResponse> detail(@PathVariable final String id) {
        return AdminApiResponse.of(membershipPolicyDetailUseCase.execute(id));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_POLICIES)
    AdminApiResponse<MembershipPolicyCreateResponse> create(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipPolicyCreateRequest request
    ) {
        return AdminApiResponse.of(membershipPolicyCreateUseCase.execute(adminId, request));
    }

    @PutMapping(AdminApiPath.MEMBERSHIP_POLICY)
    AdminApiResponse<MembershipPolicyUpdateResponse> update(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final MembershipPolicyUpdateRequest request
    ) {
        return AdminApiResponse.of(membershipPolicyUpdateUseCase.execute(adminId, id, request));
    }

    @PutMapping(AdminApiPath.MEMBERSHIP_POLICY_STATUS)
    AdminApiResponse<MembershipPolicyChangeStatusResponse> changeStatus(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final MembershipPolicyChangeStatusRequest request
    ) {
        return AdminApiResponse.of(membershipPolicyChangeStatusUseCase.execute(adminId, id, request));
    }
}
