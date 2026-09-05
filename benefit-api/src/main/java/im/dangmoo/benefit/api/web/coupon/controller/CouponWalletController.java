package im.dangmoo.benefit.api.web.coupon.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueAvailabilityResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.api.web.coupon.service.CouponWalletService;
import org.springframework.web.bind.annotation.*;

@RestController
public class CouponWalletController {

    private final CouponWalletService couponWalletService;

    public CouponWalletController(final CouponWalletService couponWalletService) {
        this.couponWalletService = couponWalletService;
    }

    @PostMapping(ApiPath.COUPON_WALLET_ISSUE_CHECK)
    ApiResponse<CouponWalletIssueAvailabilityResponse> checkIssue(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestHeader(value = UserHeaders.SEGMENT_IDS, required = false) final String segmentIds,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return ApiResponse.of(couponWalletService.checkIssue(
            userId,
            UserHeaders.parseSegmentIds(segmentIds),
            request
        ));
    }

    @PostMapping(ApiPath.COUPON_WALLET_ISSUE)
    ApiResponse<CouponWalletResponse> issue(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestHeader(value = UserHeaders.SEGMENT_IDS, required = false) final String segmentIds,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return ApiResponse.of(couponWalletService.issue(
            userId,
            UserHeaders.parseSegmentIds(segmentIds),
            request
        ));
    }

    @PostMapping(ApiPath.COUPON_WALLET_USE)
    ApiResponse<CouponWalletResponse> use(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @PathVariable final String walletId,
        @RequestBody final CouponWalletUseRequest request
    ) {
        return ApiResponse.of(couponWalletService.use(userId, walletId, request));
    }

    @PostMapping(ApiPath.COUPON_WALLET_CANCEL)
    ApiResponse<CouponWalletResponse> cancel(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @PathVariable final String walletId
    ) {
        return ApiResponse.of(couponWalletService.cancel(userId, walletId));
    }
}
