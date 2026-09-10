package im.dangmoo.benefit.admin.web.coupon.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicySearchRequest;
import im.dangmoo.benefit.admin.web.coupon.service.CouponPolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    public CouponPolicyController(final CouponPolicyService couponPolicyService) {
        this.couponPolicyService = couponPolicyService;
    }

    @GetMapping(AdminApiPath.COUPON_POLICIES)
    ApiResponse<List<CouponPolicyResponse>> list(final CouponPolicySearchRequest request) {
        return ApiResponse.of(couponPolicyService.list(request));
    }

    @GetMapping(AdminApiPath.COUPON_POLICY)
    ApiResponse<CouponPolicyResponse> get(@PathVariable final String policyId) {
        return ApiResponse.of(couponPolicyService.get(policyId));
    }

    @PostMapping(AdminApiPath.COUPON_POLICIES)
    ApiResponse<CouponPolicyResponse> create(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final CouponPolicyRequest request
    ) {
        return ApiResponse.of(couponPolicyService.create(adminId, request));
    }

    @PutMapping(AdminApiPath.COUPON_POLICY)
    ApiResponse<CouponPolicyResponse> update(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId,
        @RequestBody final CouponPolicyRequest request
    ) {
        return ApiResponse.of(couponPolicyService.update(adminId, policyId, request));
    }

    @PostMapping(AdminApiPath.COUPON_POLICY_ACTIVATE)
    ApiResponse<CouponPolicyResponse> activate(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(couponPolicyService.activate(adminId, policyId));
    }

    @PostMapping(AdminApiPath.COUPON_POLICY_SUSPEND)
    ApiResponse<CouponPolicyResponse> suspend(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(couponPolicyService.suspend(adminId, policyId));
    }
}
