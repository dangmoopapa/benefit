package im.dangmoo.benefit.admin.web.coupon.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.ApiPath;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicySearchRequest;
import im.dangmoo.benefit.admin.web.coupon.service.CouponPolicyAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponPolicyAdminController {

    private final CouponPolicyAdminService couponPolicyAdminService;

    public CouponPolicyAdminController(final CouponPolicyAdminService couponPolicyAdminService) {
        this.couponPolicyAdminService = couponPolicyAdminService;
    }

    @GetMapping(ApiPath.COUPON_POLICIES)
    ApiResponse<List<CouponPolicyResponse>> list(final CouponPolicySearchRequest request) {
        return ApiResponse.of(couponPolicyAdminService.list(request));
    }

    @GetMapping(ApiPath.COUPON_POLICY)
    ApiResponse<CouponPolicyResponse> get(@PathVariable final String policyId) {
        return ApiResponse.of(couponPolicyAdminService.get(policyId));
    }

    @PostMapping(ApiPath.COUPON_POLICIES)
    ApiResponse<CouponPolicyResponse> create(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final CouponPolicyRequest request
    ) {
        return ApiResponse.of(couponPolicyAdminService.create(adminId, request));
    }

    @PutMapping(ApiPath.COUPON_POLICY)
    ApiResponse<CouponPolicyResponse> update(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId,
        @RequestBody final CouponPolicyRequest request
    ) {
        return ApiResponse.of(couponPolicyAdminService.update(adminId, policyId, request));
    }

    @PostMapping(ApiPath.COUPON_POLICY_ACTIVATE)
    ApiResponse<CouponPolicyResponse> activate(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(couponPolicyAdminService.activate(adminId, policyId));
    }

    @PostMapping(ApiPath.COUPON_POLICY_SUSPEND)
    ApiResponse<CouponPolicyResponse> suspend(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(couponPolicyAdminService.suspend(adminId, policyId));
    }
}
