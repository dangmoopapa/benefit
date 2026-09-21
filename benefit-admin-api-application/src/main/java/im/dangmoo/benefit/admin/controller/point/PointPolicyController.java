package im.dangmoo.benefit.admin.controller.point;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyChangeStatusRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyChangeStatusResponse;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyCreateRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyCreateResponse;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyDetailResponse;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicySearchRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicySearchResponse;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyUpdateRequest;
import im.dangmoo.benefit.admin.model.point.policy.PointPolicyUpdateResponse;
import im.dangmoo.benefit.admin.usecase.point.PointPolicyChangeStatusUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointPolicyCreateUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointPolicyDetailUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointPolicySearchUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointPolicyUpdateUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointPolicyController {

    private final PointPolicySearchUseCase pointPolicySearchUseCase;
    private final PointPolicyDetailUseCase pointPolicyDetailUseCase;
    private final PointPolicyCreateUseCase pointPolicyCreateUseCase;
    private final PointPolicyUpdateUseCase pointPolicyUpdateUseCase;
    private final PointPolicyChangeStatusUseCase pointPolicyChangeStatusUseCase;

    public PointPolicyController(
        final PointPolicySearchUseCase pointPolicySearchUseCase,
        final PointPolicyDetailUseCase pointPolicyDetailUseCase,
        final PointPolicyCreateUseCase pointPolicyCreateUseCase,
        final PointPolicyUpdateUseCase pointPolicyUpdateUseCase,
        final PointPolicyChangeStatusUseCase pointPolicyChangeStatusUseCase
    ) {
        this.pointPolicySearchUseCase = pointPolicySearchUseCase;
        this.pointPolicyDetailUseCase = pointPolicyDetailUseCase;
        this.pointPolicyCreateUseCase = pointPolicyCreateUseCase;
        this.pointPolicyUpdateUseCase = pointPolicyUpdateUseCase;
        this.pointPolicyChangeStatusUseCase = pointPolicyChangeStatusUseCase;
    }

    @GetMapping(AdminApiPath.POINT_POLICIES)
    AdminApiResponse<PointPolicySearchResponse> search(@ModelAttribute final PointPolicySearchRequest request) {
        return AdminApiResponse.of(pointPolicySearchUseCase.execute(request));
    }

    @GetMapping(AdminApiPath.POINT_POLICY)
    AdminApiResponse<PointPolicyDetailResponse> detail(@PathVariable final String id) {
        return AdminApiResponse.of(pointPolicyDetailUseCase.execute(id));
    }

    @PostMapping(AdminApiPath.POINT_POLICIES)
    AdminApiResponse<PointPolicyCreateResponse> create(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointPolicyCreateRequest request
    ) {
        return AdminApiResponse.of(pointPolicyCreateUseCase.execute(adminId, request));
    }

    @PutMapping(AdminApiPath.POINT_POLICY)
    AdminApiResponse<PointPolicyUpdateResponse> update(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PointPolicyUpdateRequest request
    ) {
        return AdminApiResponse.of(pointPolicyUpdateUseCase.execute(adminId, id, request));
    }

    @PutMapping(AdminApiPath.POINT_POLICY_STATUS)
    AdminApiResponse<PointPolicyChangeStatusResponse> changeStatus(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @PathVariable final String id,
        @RequestBody final PointPolicyChangeStatusRequest request
    ) {
        return AdminApiResponse.of(pointPolicyChangeStatusUseCase.execute(adminId, id, request));
    }
}
