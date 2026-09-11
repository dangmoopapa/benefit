package im.dangmoo.benefit.admin.web.membership.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicyRequest;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicyResponse;
import im.dangmoo.benefit.admin.web.membership.model.MembershipPolicySearchRequest;
import im.dangmoo.benefit.admin.web.membership.service.MembershipPolicyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MembershipPolicyController {

    private final MembershipPolicyService membershipPolicyService;

    public MembershipPolicyController(final MembershipPolicyService membershipPolicyService) {
        this.membershipPolicyService = membershipPolicyService;
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_POLICIES)
    ApiResponse<List<MembershipPolicyResponse>> list(final MembershipPolicySearchRequest request) {
        return ApiResponse.of(membershipPolicyService.list(request));
    }

    @GetMapping(AdminApiPath.MEMBERSHIP_POLICY)
    ApiResponse<MembershipPolicyResponse> get(@PathVariable final String policyId) {
        return ApiResponse.of(membershipPolicyService.get(policyId));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_POLICIES)
    ApiResponse<MembershipPolicyResponse> create(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipPolicyRequest request
    ) {
        return ApiResponse.of(membershipPolicyService.create(adminId, request));
    }

    @PutMapping(AdminApiPath.MEMBERSHIP_POLICY)
    ApiResponse<MembershipPolicyResponse> update(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId,
        @RequestBody final MembershipPolicyRequest request
    ) {
        return ApiResponse.of(membershipPolicyService.update(adminId, policyId, request));
    }
}
