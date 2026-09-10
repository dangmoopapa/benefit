package im.dangmoo.benefit.api.web.coupon.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.api.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.api.web.coupon.service.CouponUseService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponUseController {

    private final CouponUseService couponUseService;

    public CouponUseController(final CouponUseService couponUseService) {
        this.couponUseService = couponUseService;
    }

    @PostMapping(ApiPath.COUPON_USE)
    ApiResponse<CouponWalletResponse> use(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @PathVariable final String walletId,
        @RequestBody final CouponWalletUseRequest request
    ) {
        return ApiResponse.of(couponUseService.use(userId, walletId, request));
    }

    @PostMapping(ApiPath.COUPON_CANCEL)
    ApiResponse<CouponWalletResponse> cancel(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @PathVariable final String walletId
    ) {
        return ApiResponse.of(couponUseService.cancel(userId, walletId));
    }
}
