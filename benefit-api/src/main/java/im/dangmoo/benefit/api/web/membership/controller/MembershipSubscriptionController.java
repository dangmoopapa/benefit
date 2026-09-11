package im.dangmoo.benefit.api.web.membership.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.membership.model.MembershipResponse;
import im.dangmoo.benefit.api.web.membership.service.MembershipSubscriptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MembershipSubscriptionController {

    private final MembershipSubscriptionService membershipSubscriptionService;

    public MembershipSubscriptionController(final MembershipSubscriptionService membershipSubscriptionService) {
        this.membershipSubscriptionService = membershipSubscriptionService;
    }

    @GetMapping(ApiPath.MEMBERSHIP)
    ApiResponse<MembershipResponse> get(@RequestHeader(UserHeaders.USER_ID) final String userId) {
        return ApiResponse.of(membershipSubscriptionService.get(userId));
    }

    @PostMapping(ApiPath.MEMBERSHIP)
    ApiResponse<MembershipResponse> join(@RequestHeader(UserHeaders.USER_ID) final String userId) {
        return ApiResponse.of(membershipSubscriptionService.join(userId));
    }

    @PostMapping(ApiPath.MEMBERSHIP_CANCEL)
    ApiResponse<MembershipResponse> cancel(@RequestHeader(UserHeaders.USER_ID) final String userId) {
        return ApiResponse.of(membershipSubscriptionService.cancel(userId));
    }
}
