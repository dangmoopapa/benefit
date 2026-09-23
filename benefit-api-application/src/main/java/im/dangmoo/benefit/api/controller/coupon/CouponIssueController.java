package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.dto.coupon.CouponIssuableRequest;
import im.dangmoo.benefit.api.dto.coupon.CouponIssuableResponse;
import im.dangmoo.benefit.api.dto.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.dto.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.coupon.CouponIssuableUseCase;
import im.dangmoo.benefit.api.usecase.coupon.CouponIssueUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponIssueController {

    private final CouponIssuableUseCase couponIssuableUseCase;
    private final CouponIssueUseCase couponIssueUseCase;

    public CouponIssueController(
        final CouponIssuableUseCase couponIssuableUseCase,
        final CouponIssueUseCase couponIssueUseCase
    ) {
        this.couponIssuableUseCase = couponIssuableUseCase;
        this.couponIssueUseCase = couponIssueUseCase;
    }

    @PostMapping(ApiPath.COUPON_ISSUABLE)
    ApiResponse<CouponIssuableResponse> issuable(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponIssuableRequest request
    ) {
        return ApiResponse.of(couponIssuableUseCase.issuable(userId, request, false));
    }

    @PostMapping(ApiPath.COUPON_ISSUE)
    ApiResponse<CouponIssueResponse> issue(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponIssueRequest request
    ) {
        return ApiResponse.of(couponIssueUseCase.issue(userId, request));
    }
}
