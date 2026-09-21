package im.dangmoo.benefit.api.controller.point;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.point.PointGrantRequest;
import im.dangmoo.benefit.api.model.point.PointGrantResponse;
import im.dangmoo.benefit.api.model.point.PointReclaimRequest;
import im.dangmoo.benefit.api.model.point.PointReclaimResponse;
import im.dangmoo.benefit.api.model.point.PointRecoveryRequest;
import im.dangmoo.benefit.api.model.point.PointRecoveryResponse;
import im.dangmoo.benefit.api.model.point.PointTransactionListResponse;
import im.dangmoo.benefit.api.model.point.PointTransactionPageRequest;
import im.dangmoo.benefit.api.model.point.PointUsageRequest;
import im.dangmoo.benefit.api.model.point.PointUsageResponse;
import im.dangmoo.benefit.api.usecase.point.PointTransactionGrantUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionReclaimUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionRecoveryUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionSearchUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionUsageUseCase;
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

    @GetMapping(ApiPath.POINT_TRANSACTIONS)
    ApiResponse<PointTransactionListResponse> list(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @ModelAttribute final PointTransactionPageRequest request
    ) {
        return ApiResponse.of(pointTransactionSearchUseCase.execute(userId, request));
    }

    @PostMapping(ApiPath.POINT_TRANSACTION_GRANT)
    ApiResponse<PointGrantResponse> grant(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointGrantRequest request
    ) {
        return ApiResponse.of(pointTransactionGrantUseCase.execute(userId, request));
    }

    @PostMapping(ApiPath.POINT_TRANSACTION_RECLAIM)
    ApiResponse<PointReclaimResponse> reclaim(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointReclaimRequest request
    ) {
        return ApiResponse.of(pointTransactionReclaimUseCase.execute(userId, request));
    }

    @PostMapping(ApiPath.POINT_TRANSACTION_USAGE)
    ApiResponse<PointUsageResponse> use(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointUsageRequest request
    ) {
        return ApiResponse.of(pointTransactionUsageUseCase.execute(userId, request));
    }

    @PostMapping(ApiPath.POINT_TRANSACTION_RECOVERY)
    ApiResponse<PointRecoveryResponse> recover(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointRecoveryRequest request
    ) {
        return ApiResponse.of(pointTransactionRecoveryUseCase.execute(userId, request));
    }
}
