package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.coupon.CouponIssuableRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssuableResponse;
import im.dangmoo.benefit.api.model.coupon.CouponIssueRequest;
import im.dangmoo.benefit.api.model.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.usecase.coupon.CouponIssuableUseCase;
import im.dangmoo.benefit.api.usecase.coupon.CouponTimeAttackIssueUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponTimeAttackController {

    private final CouponIssuableUseCase couponIssuableUseCase;
    private final CouponTimeAttackIssueUseCase couponTimeAttackIssueUseCase;

    public CouponTimeAttackController(
        final CouponIssuableUseCase couponIssuableUseCase,
        final CouponTimeAttackIssueUseCase couponTimeAttackIssueUseCase
    ) {
        this.couponIssuableUseCase = couponIssuableUseCase;
        this.couponTimeAttackIssueUseCase = couponTimeAttackIssueUseCase;
    }

    @PostMapping(ApiPath.COUPON_TIME_ATTACK_ISSUABLE)
    ApiResponse<CouponIssuableResponse> issuable(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponIssuableRequest request
    ) {
        return ApiResponse.of(couponIssuableUseCase.execute(userId, request, true));
    }

    @PostMapping(ApiPath.COUPON_TIME_ATTACK_ISSUE)
    ApiResponse<CouponIssueResponse> issue(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponIssueRequest request
    ) {
        return ApiResponse.of(couponTimeAttackIssueUseCase.execute(userId, request));
    }
}
