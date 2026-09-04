package im.dangmoo.benefit.admin.web.coupon.controller;

import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.ApiPath;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.admin.web.coupon.service.CouponWalletAdminService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(ApiPath.COUPON_WALLET_ISSUE)
    ApiResponse<CouponWalletResponse> issue(@RequestBody final CouponWalletIssueRequest request) {
        return ApiResponse.of(couponWalletAdminService.issue(request));
    }

    @PostMapping(ApiPath.COUPON_WALLET_USE)
    ApiResponse<CouponWalletResponse> use(
        @PathVariable final String walletId,
        @RequestBody final CouponWalletUseRequest request
    ) {
        return ApiResponse.of(couponWalletAdminService.use(walletId, request));
    }

    @PostMapping(ApiPath.COUPON_WALLET_RECOVER)
    ApiResponse<CouponWalletResponse> recover(@PathVariable final String walletId) {
        return ApiResponse.of(couponWalletAdminService.recover(walletId));
    }
}
