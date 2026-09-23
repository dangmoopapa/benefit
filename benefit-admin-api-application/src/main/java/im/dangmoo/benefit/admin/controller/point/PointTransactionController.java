package im.dangmoo.benefit.admin.controller.point;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.dto.point.grant.PointGrantRequest;
import im.dangmoo.benefit.admin.dto.point.grant.PointGrantResponse;
import im.dangmoo.benefit.admin.dto.point.reclaim.PointReclaimRequest;
import im.dangmoo.benefit.admin.dto.point.reclaim.PointReclaimResponse;
import im.dangmoo.benefit.admin.dto.point.recovery.PointRecoveryRequest;
import im.dangmoo.benefit.admin.dto.point.recovery.PointRecoveryResponse;
import im.dangmoo.benefit.admin.dto.point.transaction.PointTransactionSearchRequest;
import im.dangmoo.benefit.admin.dto.point.transaction.PointTransactionSearchResponse;
import im.dangmoo.benefit.admin.dto.point.usage.PointUsageRequest;
import im.dangmoo.benefit.admin.dto.point.usage.PointUsageResponse;
import im.dangmoo.benefit.admin.usecase.point.PointTransactionGrantUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointTransactionReclaimUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointTransactionRecoveryUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointTransactionSearchUseCase;
import im.dangmoo.benefit.admin.usecase.point.PointTransactionUsageUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointTransactionController {

    private final PointTransactionSearchUseCase pointTransactionSearchUseCase;
    private final PointTransactionGrantUseCase pointTransactionGrantUseCase;
    private final PointTransactionReclaimUseCase pointTransactionReclaimUseCase;
    private final PointTransactionUsageUseCase pointTransactionUsageUseCase;
    private final PointTransactionRecoveryUseCase pointTransactionRecoveryUseCase;

    public PointTransactionController(
        final PointTransactionSearchUseCase pointTransactionSearchUseCase,
        final PointTransactionGrantUseCase pointTransactionGrantUseCase,
        final PointTransactionReclaimUseCase pointTransactionReclaimUseCase,
        final PointTransactionUsageUseCase pointTransactionUsageUseCase,
        final PointTransactionRecoveryUseCase pointTransactionRecoveryUseCase
    ) {
        this.pointTransactionSearchUseCase = pointTransactionSearchUseCase;
        this.pointTransactionGrantUseCase = pointTransactionGrantUseCase;
        this.pointTransactionReclaimUseCase = pointTransactionReclaimUseCase;
        this.pointTransactionUsageUseCase = pointTransactionUsageUseCase;
        this.pointTransactionRecoveryUseCase = pointTransactionRecoveryUseCase;
    }

    @GetMapping(AdminApiPath.POINT_TRANSACTIONS)
    AdminApiResponse<PointTransactionSearchResponse> search(
        @ModelAttribute final PointTransactionSearchRequest request
    ) {
        return AdminApiResponse.of(pointTransactionSearchUseCase.search(request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_GRANT)
    AdminApiResponse<PointGrantResponse> grant(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointGrantRequest request
    ) {
        return AdminApiResponse.of(pointTransactionGrantUseCase.grant(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_RECLAIM)
    AdminApiResponse<PointReclaimResponse> reclaim(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointReclaimRequest request
    ) {
        return AdminApiResponse.of(pointTransactionReclaimUseCase.reclaim(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_USAGE)
    AdminApiResponse<PointUsageResponse> use(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointUsageRequest request
    ) {
        return AdminApiResponse.of(pointTransactionUsageUseCase.use(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_RECOVERY)
    AdminApiResponse<PointRecoveryResponse> recover(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointRecoveryRequest request
    ) {
        return AdminApiResponse.of(pointTransactionRecoveryUseCase.recover(adminId, request));
    }
}
