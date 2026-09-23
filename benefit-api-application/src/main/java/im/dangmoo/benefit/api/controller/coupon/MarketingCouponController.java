package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.dto.coupon.CouponIssueResponse;
import im.dangmoo.benefit.api.dto.coupon.MarketingCouponIssueRequest;
import im.dangmoo.benefit.api.usecase.coupon.CouponIssueUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketingCouponController {

    private final CouponIssueUseCase couponIssueUseCase;

    public MarketingCouponController(final CouponIssueUseCase couponIssueUseCase) {
        this.couponIssueUseCase = couponIssueUseCase;
    }

    @PostMapping(ApiPath.COUPON_MARKETING_ISSUE)
    ApiResponse<CouponIssueResponse> issue(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final MarketingCouponIssueRequest request
    ) {
        return ApiResponse.of(couponIssueUseCase.issueMarketing(userId, request));
    }
}
