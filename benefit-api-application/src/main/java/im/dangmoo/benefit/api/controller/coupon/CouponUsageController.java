package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.coupon.CouponRecoveryRequest;
import im.dangmoo.benefit.api.model.coupon.CouponRecoveryResponse;
import im.dangmoo.benefit.api.model.coupon.CouponUsableRequest;
import im.dangmoo.benefit.api.model.coupon.CouponUsableResponse;
import im.dangmoo.benefit.api.model.coupon.CouponUsageRequest;
import im.dangmoo.benefit.api.model.coupon.CouponUsageResponse;
import im.dangmoo.benefit.api.usecase.coupon.CouponRecoveryUseCase;
import im.dangmoo.benefit.api.usecase.coupon.CouponUsableUseCase;
import im.dangmoo.benefit.api.usecase.coupon.CouponUsageUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponUsageController {

    private final CouponUsableUseCase couponUsableUseCase;
    private final CouponUsageUseCase couponUsageUseCase;
    private final CouponRecoveryUseCase couponRecoveryUseCase;

    public CouponUsageController(
        final CouponUsableUseCase couponUsableUseCase,
        final CouponUsageUseCase couponUsageUseCase,
        final CouponRecoveryUseCase couponRecoveryUseCase
    ) {
        this.couponUsableUseCase = couponUsableUseCase;
        this.couponUsageUseCase = couponUsageUseCase;
        this.couponRecoveryUseCase = couponRecoveryUseCase;
    }

    @PostMapping(ApiPath.COUPON_USABLE)
    ApiResponse<CouponUsableResponse> usable(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponUsableRequest request
    ) {
        return ApiResponse.of(couponUsableUseCase.usable(userId, request));
    }

    @PostMapping(ApiPath.COUPON_USAGE)
    ApiResponse<CouponUsageResponse> usage(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponUsageRequest request
    ) {
        return ApiResponse.of(couponUsageUseCase.use(userId, request));
    }

    @PostMapping(ApiPath.COUPON_RECOVERY)
    ApiResponse<CouponRecoveryResponse> recovery(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final CouponRecoveryRequest request
    ) {
        return ApiResponse.of(couponRecoveryUseCase.recover(userId, request));
    }
}
