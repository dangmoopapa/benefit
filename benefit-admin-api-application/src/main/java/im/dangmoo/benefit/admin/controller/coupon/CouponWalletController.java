package im.dangmoo.benefit.admin.controller.coupon;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletIssueRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletIssueResponse;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletRecoveryResponse;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletSearchRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletSearchResponse;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletUsageRequest;
import im.dangmoo.benefit.admin.dto.coupon.wallet.CouponWalletUsageResponse;
import im.dangmoo.benefit.admin.usecase.coupon.CouponWalletIssueUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponWalletRecoveryUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponWalletSearchUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponWalletUsageUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponWalletController {

    private final CouponWalletSearchUseCase couponWalletSearchUseCase;
    private final CouponWalletIssueUseCase couponWalletIssueUseCase;
    private final CouponWalletUsageUseCase couponWalletUsageUseCase;
    private final CouponWalletRecoveryUseCase couponWalletRecoveryUseCase;

    public CouponWalletController(
        final CouponWalletSearchUseCase couponWalletSearchUseCase,
        final CouponWalletIssueUseCase couponWalletIssueUseCase,
        final CouponWalletUsageUseCase couponWalletUsageUseCase,
        final CouponWalletRecoveryUseCase couponWalletRecoveryUseCase
    ) {
        this.couponWalletSearchUseCase = couponWalletSearchUseCase;
        this.couponWalletIssueUseCase = couponWalletIssueUseCase;
        this.couponWalletUsageUseCase = couponWalletUsageUseCase;
        this.couponWalletRecoveryUseCase = couponWalletRecoveryUseCase;
    }

    @GetMapping(AdminApiPath.COUPON_WALLETS)
    AdminApiResponse<CouponWalletSearchResponse> search(@ModelAttribute final CouponWalletSearchRequest request) {
        return AdminApiResponse.of(couponWalletSearchUseCase.search(request));
    }

    @PostMapping(AdminApiPath.COUPON_WALLETS)
    AdminApiResponse<CouponWalletIssueResponse> issue(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final CouponWalletIssueRequest request
    ) {
        return AdminApiResponse.of(couponWalletIssueUseCase.issue(adminId, request));
    }

    @PostMapping(AdminApiPath.COUPON_WALLET_USAGE)
    AdminApiResponse<CouponWalletUsageResponse> usage(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final CouponWalletUsageRequest request
    ) {
        return AdminApiResponse.of(couponWalletUsageUseCase.use(adminId, id, request));
    }

    @PostMapping(AdminApiPath.COUPON_WALLET_RECOVERY)
    AdminApiResponse<CouponWalletRecoveryResponse> recovery(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id
    ) {
        return AdminApiResponse.of(couponWalletRecoveryUseCase.recover(adminId, id));
    }
}
