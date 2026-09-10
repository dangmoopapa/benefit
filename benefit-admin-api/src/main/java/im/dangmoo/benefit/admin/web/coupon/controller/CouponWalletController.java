package im.dangmoo.benefit.admin.web.coupon.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponWalletUseRequest;
import im.dangmoo.benefit.admin.web.coupon.service.CouponWalletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CouponWalletController {

    private final CouponWalletService couponWalletService;

    public CouponWalletController(final CouponWalletService couponWalletService) {
        this.couponWalletService = couponWalletService;
    }

    @GetMapping(AdminApiPath.COUPON_WALLETS_BY_POLICY)
    ApiResponse<List<CouponWalletResponse>> listByPolicy(@RequestParam final String policyId) {
        return ApiResponse.of(couponWalletService.listByPolicyId(policyId));
    }

    @GetMapping(AdminApiPath.COUPON_WALLETS_BY_USER)
    ApiResponse<List<CouponWalletResponse>> listByUser(@RequestParam final String userId) {
        return ApiResponse.of(couponWalletService.listByUserId(userId));
    }

    @PostMapping(AdminApiPath.COUPON_WALLETS)
    ApiResponse<CouponWalletResponse> issue(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return ApiResponse.of(couponWalletService.issue(adminId, request));
    }

    @PostMapping(AdminApiPath.COUPON_WALLET_USE)
    ApiResponse<CouponWalletResponse> use(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String walletId,
        @RequestBody final CouponWalletUseRequest request
    ) {
        return ApiResponse.of(couponWalletService.use(adminId, walletId, request));
    }

    @PostMapping(AdminApiPath.COUPON_WALLET_RECOVER)
    ApiResponse<CouponWalletResponse> recover(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String walletId
    ) {
        return ApiResponse.of(couponWalletService.recover(adminId, walletId));
    }
}
