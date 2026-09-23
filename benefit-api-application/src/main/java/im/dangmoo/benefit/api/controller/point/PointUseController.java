package im.dangmoo.benefit.api.controller.point;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.dto.point.PointRecoveryRequest;
import im.dangmoo.benefit.api.dto.point.PointRecoveryResponse;
import im.dangmoo.benefit.api.dto.point.PointUsageRequest;
import im.dangmoo.benefit.api.dto.point.PointUsageResponse;
import im.dangmoo.benefit.api.usecase.point.PointTransactionRecoveryUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionUsageUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointUseController {

    private final PointTransactionUsageUseCase pointTransactionUsageUseCase;
    private final PointTransactionRecoveryUseCase pointTransactionRecoveryUseCase;

    public PointUseController(
        final PointTransactionUsageUseCase pointTransactionUsageUseCase,
        final PointTransactionRecoveryUseCase pointTransactionRecoveryUseCase
    ) {
        this.pointTransactionUsageUseCase = pointTransactionUsageUseCase;
        this.pointTransactionRecoveryUseCase = pointTransactionRecoveryUseCase;
    }

    @PostMapping(ApiPath.POINT_USAGE)
    ApiResponse<PointUsageResponse> usage(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointUsageRequest request
    ) {
        return ApiResponse.of(pointTransactionUsageUseCase.use(userId, request));
    }

    @PostMapping(ApiPath.POINT_RECOVERY)
    ApiResponse<PointRecoveryResponse> recovery(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointRecoveryRequest request
    ) {
        return ApiResponse.of(pointTransactionRecoveryUseCase.recover(userId, request));
    }
}
