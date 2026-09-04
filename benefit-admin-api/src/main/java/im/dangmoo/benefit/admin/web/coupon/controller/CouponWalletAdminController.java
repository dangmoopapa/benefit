package im.dangmoo.benefit.admin.web.coupon.controller;

import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.ApiPath;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.service.CouponWalletAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CouponWalletAdminController {

    private final CouponWalletAdminService couponWalletAdminService;

    public CouponWalletAdminController(final CouponWalletAdminService couponWalletAdminService) {
        this.couponWalletAdminService = couponWalletAdminService;
    }

    @GetMapping(ApiPath.COUPON_WALLETS_BY_POLICY)
    ApiResponse<List<CouponWalletResponse>> listByPolicy(@RequestParam final String policyId) {
        return ApiResponse.of(couponWalletAdminService.listByPolicyId(policyId));
    }

    @GetMapping(ApiPath.COUPON_WALLETS_BY_USER)
    ApiResponse<List<CouponWalletResponse>> listByUser(@RequestParam final String userId) {
        return ApiResponse.of(couponWalletAdminService.listByUserId(userId));
    }
}
