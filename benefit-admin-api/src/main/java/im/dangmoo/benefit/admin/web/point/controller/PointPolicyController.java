package im.dangmoo.benefit.admin.web.point.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.point.model.PointPolicyRequest;
import im.dangmoo.benefit.admin.web.point.model.PointPolicyResponse;
import im.dangmoo.benefit.admin.web.point.model.PointPolicySearchRequest;
import im.dangmoo.benefit.admin.web.point.service.PointPolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    public PointPolicyController(final PointPolicyService pointPolicyService) {
        this.pointPolicyService = pointPolicyService;
    }

    @GetMapping(AdminApiPath.POINT_POLICIES)
    ApiResponse<List<PointPolicyResponse>> list(final PointPolicySearchRequest request) {
        return ApiResponse.of(pointPolicyService.list(request));
    }

    @GetMapping(AdminApiPath.POINT_POLICY)
    ApiResponse<PointPolicyResponse> get(@PathVariable final String policyId) {
        return ApiResponse.of(pointPolicyService.get(policyId));
    }

    @PostMapping(AdminApiPath.POINT_POLICIES)
    ApiResponse<PointPolicyResponse> create(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointPolicyRequest request
    ) {
        return ApiResponse.of(pointPolicyService.create(adminId, request));
    }

    @PutMapping(AdminApiPath.POINT_POLICY)
    ApiResponse<PointPolicyResponse> update(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId,
        @RequestBody final PointPolicyRequest request
    ) {
        return ApiResponse.of(pointPolicyService.update(adminId, policyId, request));
    }

    @PostMapping(AdminApiPath.POINT_POLICY_ACTIVATE)
    ApiResponse<PointPolicyResponse> activate(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(pointPolicyService.activate(adminId, policyId));
    }

    @PostMapping(AdminApiPath.POINT_POLICY_SUSPEND)
    ApiResponse<PointPolicyResponse> suspend(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String policyId
    ) {
        return ApiResponse.of(pointPolicyService.suspend(adminId, policyId));
    }
}
