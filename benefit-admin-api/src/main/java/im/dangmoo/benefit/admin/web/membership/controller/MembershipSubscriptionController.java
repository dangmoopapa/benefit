package im.dangmoo.benefit.admin.web.membership.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.membership.model.MembershipCancelRequest;
import im.dangmoo.benefit.admin.web.membership.model.MembershipResponse;
import im.dangmoo.benefit.admin.web.membership.service.MembershipSubscriptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipSubscriptionController {

    private final MembershipSubscriptionService membershipSubscriptionService;

    public MembershipSubscriptionController(final MembershipSubscriptionService membershipSubscriptionService) {
        this.membershipSubscriptionService = membershipSubscriptionService;
    }

    @GetMapping(AdminApiPath.MEMBERSHIPS)
    ApiResponse<MembershipResponse> get(@RequestParam final String userId) {
        return ApiResponse.of(membershipSubscriptionService.get(userId));
    }

    @PostMapping(AdminApiPath.MEMBERSHIP_CANCEL)
    ApiResponse<MembershipResponse> cancel(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final MembershipCancelRequest request
    ) {
        return ApiResponse.of(membershipSubscriptionService.cancel(adminId, request.userId()));
    }
}
