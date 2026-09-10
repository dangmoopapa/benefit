package im.dangmoo.benefit.api.web.coupon.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueAvailabilityResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.service.CouponIssueService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponIssueController {

    private final CouponIssueService couponIssueService;

    public CouponIssueController(final CouponIssueService couponIssueService) {
        this.couponIssueService = couponIssueService;
    }

    @PostMapping(ApiPath.COUPON_ISSUE_CHECK)
    ApiResponse<CouponWalletIssueAvailabilityResponse> check(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestHeader(value = UserHeaders.SEGMENT_IDS, required = false) final String segmentIds,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return ApiResponse.of(couponIssueService.check(
            userId,
            UserHeaders.parseSegmentIds(segmentIds),
            request
        ));
    }

    @PostMapping(ApiPath.COUPON_ISSUE)
    ApiResponse<CouponWalletResponse> issue(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestHeader(value = UserHeaders.SEGMENT_IDS, required = false) final String segmentIds,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return ApiResponse.of(couponIssueService.issue(
            userId,
            UserHeaders.parseSegmentIds(segmentIds),
            request
        ));
    }
}
