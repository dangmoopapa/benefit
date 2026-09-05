package im.dangmoo.benefit.api.web.coupon.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.coupon.model.CouponBoxResponse;
import im.dangmoo.benefit.api.web.coupon.service.CouponBoxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponBoxController {

    private final CouponBoxService couponBoxService;

    public CouponBoxController(final CouponBoxService couponBoxService) {
        this.couponBoxService = couponBoxService;
    }

    @GetMapping(ApiPath.COUPON_BOX)
    ApiResponse<CouponBoxResponse> get(@RequestParam final String userId) {
        return ApiResponse.of(couponBoxService.get(userId));
    }
}
