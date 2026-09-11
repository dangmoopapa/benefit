package im.dangmoo.benefit.api.web.membership.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.membership.model.MembershipPrivilegeApplyRequest;
import im.dangmoo.benefit.api.web.membership.model.MembershipPrivilegeApplyResponse;
import im.dangmoo.benefit.api.web.membership.service.MembershipPrivilegeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipPrivilegeController {

    private final MembershipPrivilegeService membershipPrivilegeService;

    public MembershipPrivilegeController(final MembershipPrivilegeService membershipPrivilegeService) {
        this.membershipPrivilegeService = membershipPrivilegeService;
    }

    @PostMapping(ApiPath.MEMBERSHIP_PRIVILEGE_APPLY)
    ApiResponse<MembershipPrivilegeApplyResponse> apply(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestBody final MembershipPrivilegeApplyRequest request
    ) {
        return ApiResponse.of(membershipPrivilegeService.apply(userId, request));
    }

    @PostMapping(ApiPath.MEMBERSHIP_PRIVILEGE_COUPON_ISSUE)
    ApiResponse<CouponWalletResponse> issueCoupon(@RequestHeader(UserHeaders.USER_ID) final String userId) {
        return ApiResponse.of(membershipPrivilegeService.issueCoupon(userId));
    }
}
