package im.dangmoo.benefit.admin.controller.point;

import im.dangmoo.benefit.admin.controller.AdminApiHeaders;
import im.dangmoo.benefit.admin.controller.AdminApiPath;
import im.dangmoo.benefit.admin.controller.AdminApiResponse;
import im.dangmoo.benefit.admin.model.point.grant.PointGrantRequest;
import im.dangmoo.benefit.admin.model.point.grant.PointGrantResponse;
import im.dangmoo.benefit.admin.model.point.reclaim.PointReclaimRequest;
import im.dangmoo.benefit.admin.model.point.reclaim.PointReclaimResponse;
import im.dangmoo.benefit.admin.model.point.recovery.PointRecoveryRequest;
import im.dangmoo.benefit.admin.model.point.recovery.PointRecoveryResponse;
import im.dangmoo.benefit.admin.model.point.transaction.PointTransactionSearchRequest;
import im.dangmoo.benefit.admin.model.point.transaction.PointTransactionSearchResponse;
import im.dangmoo.benefit.admin.model.point.usage.PointUsageRequest;
import im.dangmoo.benefit.admin.model.point.usage.PointUsageResponse;
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
        return AdminApiResponse.of(pointTransactionSearchUseCase.execute(request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_GRANTS)
    AdminApiResponse<PointGrantResponse> grant(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointGrantRequest request
    ) {
        return AdminApiResponse.of(pointTransactionGrantUseCase.execute(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_RECLAIMS)
    AdminApiResponse<PointReclaimResponse> reclaim(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointReclaimRequest request
    ) {
        return AdminApiResponse.of(pointTransactionReclaimUseCase.execute(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_USAGES)
    AdminApiResponse<PointUsageResponse> use(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointUsageRequest request
    ) {
        return AdminApiResponse.of(pointTransactionUsageUseCase.execute(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_RECOVERIES)
    AdminApiResponse<PointRecoveryResponse> recover(
        @RequestHeader(AdminApiHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointRecoveryRequest request
    ) {
        return AdminApiResponse.of(pointTransactionRecoveryUseCase.execute(adminId, request));
    }
}
