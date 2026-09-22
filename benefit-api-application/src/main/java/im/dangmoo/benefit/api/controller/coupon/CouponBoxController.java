package im.dangmoo.benefit.api.controller.coupon;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.coupon.CouponBoxResponse;
import im.dangmoo.benefit.api.usecase.coupon.CouponBoxUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponBoxController {

    private final CouponBoxUseCase couponBoxUseCase;

    public CouponBoxController(final CouponBoxUseCase couponBoxUseCase) {
        this.couponBoxUseCase = couponBoxUseCase;
    }

    @GetMapping(ApiPath.COUPON_BOX)
    ApiResponse<CouponBoxResponse> box(@RequestHeader(ApiHeaders.USER_ID) final String userId) {
        return ApiResponse.of(couponBoxUseCase.box(userId));
    }
}
