package im.dangmoo.benefit.admin.controller.coupon;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyCreateResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyDetailResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicySearchRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicySearchResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.CouponPolicyUpdateResponse;
import im.dangmoo.benefit.admin.model.coupon.policy.VoucherCouponPolicySearchRequest;
import im.dangmoo.benefit.admin.model.coupon.policy.VoucherCouponPolicySearchResponse;
import im.dangmoo.benefit.admin.usecase.coupon.CouponPolicyChangeStatusUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponPolicyCreateUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponPolicyDetailUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponPolicySearchUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.CouponPolicyUpdateUseCase;
import im.dangmoo.benefit.admin.usecase.coupon.VoucherCouponPolicySearchUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponPolicyController {

    private final CouponPolicySearchUseCase couponPolicySearchUseCase;
    private final VoucherCouponPolicySearchUseCase voucherCouponPolicySearchUseCase;
    private final CouponPolicyDetailUseCase couponPolicyDetailUseCase;
    private final CouponPolicyCreateUseCase couponPolicyCreateUseCase;
    private final CouponPolicyUpdateUseCase couponPolicyUpdateUseCase;
    private final CouponPolicyChangeStatusUseCase couponPolicyChangeStatusUseCase;

    public CouponPolicyController(
        final CouponPolicySearchUseCase couponPolicySearchUseCase,
        final VoucherCouponPolicySearchUseCase voucherCouponPolicySearchUseCase,
        final CouponPolicyDetailUseCase couponPolicyDetailUseCase,
        final CouponPolicyCreateUseCase couponPolicyCreateUseCase,
        final CouponPolicyUpdateUseCase couponPolicyUpdateUseCase,
        final CouponPolicyChangeStatusUseCase couponPolicyChangeStatusUseCase
    ) {
        this.couponPolicySearchUseCase = couponPolicySearchUseCase;
        this.voucherCouponPolicySearchUseCase = voucherCouponPolicySearchUseCase;
        this.couponPolicyDetailUseCase = couponPolicyDetailUseCase;
        this.couponPolicyCreateUseCase = couponPolicyCreateUseCase;
        this.couponPolicyUpdateUseCase = couponPolicyUpdateUseCase;
        this.couponPolicyChangeStatusUseCase = couponPolicyChangeStatusUseCase;
    }

    @GetMapping(AdminApiPath.COUPON_POLICIES)
    AdminApiResponse<CouponPolicySearchResponse> search(@ModelAttribute final CouponPolicySearchRequest request) {
        return AdminApiResponse.of(couponPolicySearchUseCase.execute(request));
    }

    @GetMapping(AdminApiPath.COUPON_VOUCHER_POLICIES)
    AdminApiResponse<VoucherCouponPolicySearchResponse> searchVouchers(
        @ModelAttribute final VoucherCouponPolicySearchRequest request
    ) {
        return AdminApiResponse.of(voucherCouponPolicySearchUseCase.execute(request));
    }

    @GetMapping(AdminApiPath.COUPON_POLICY)
    AdminApiResponse<CouponPolicyDetailResponse> detail(@PathVariable final String id) {
        return AdminApiResponse.of(couponPolicyDetailUseCase.execute(id));
    }

    @PostMapping(AdminApiPath.COUPON_POLICIES)
    AdminApiResponse<CouponPolicyCreateResponse> create(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final CouponPolicyCreateRequest request
    ) {
        return AdminApiResponse.of(couponPolicyCreateUseCase.execute(adminId, request));
    }

    @PutMapping(AdminApiPath.COUPON_POLICY)
    AdminApiResponse<CouponPolicyUpdateResponse> update(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final CouponPolicyUpdateRequest request
    ) {
        return AdminApiResponse.of(couponPolicyUpdateUseCase.execute(adminId, id, request));
    }

    @PutMapping(AdminApiPath.COUPON_POLICY_STATUS)
    AdminApiResponse<CouponPolicyChangeStatusResponse> changeStatus(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final CouponPolicyChangeStatusRequest request
    ) {
        return AdminApiResponse.of(couponPolicyChangeStatusUseCase.execute(adminId, id, request));
    }
}
