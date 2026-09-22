package im.dangmoo.benefit.api.controller.point;

import im.dangmoo.benefit.api.controller.ApiHeaders;
import im.dangmoo.benefit.api.controller.ApiPath;
import im.dangmoo.benefit.api.controller.ApiResponse;
import im.dangmoo.benefit.api.model.point.PointGrantRequest;
import im.dangmoo.benefit.api.model.point.PointGrantResponse;
import im.dangmoo.benefit.api.model.point.PointIssuableRequest;
import im.dangmoo.benefit.api.model.point.PointIssuableResponse;
import im.dangmoo.benefit.api.model.point.PointReclaimRequest;
import im.dangmoo.benefit.api.model.point.PointReclaimResponse;
import im.dangmoo.benefit.api.usecase.point.PointIssuableUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionGrantUseCase;
import im.dangmoo.benefit.api.usecase.point.PointTransactionReclaimUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointIssueController {

    private final PointIssuableUseCase pointIssuableUseCase;
    private final PointTransactionGrantUseCase pointTransactionGrantUseCase;
    private final PointTransactionReclaimUseCase pointTransactionReclaimUseCase;

    public PointIssueController(
        final PointIssuableUseCase pointIssuableUseCase,
        final PointTransactionGrantUseCase pointTransactionGrantUseCase,
        final PointTransactionReclaimUseCase pointTransactionReclaimUseCase
    ) {
        this.pointIssuableUseCase = pointIssuableUseCase;
        this.pointTransactionGrantUseCase = pointTransactionGrantUseCase;
        this.pointTransactionReclaimUseCase = pointTransactionReclaimUseCase;
    }

    @PostMapping(ApiPath.POINT_ISSUABLE)
    ApiResponse<PointIssuableResponse> issuable(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointIssuableRequest request
    ) {
        return ApiResponse.of(pointIssuableUseCase.issuable(userId, request));
    }

    @PostMapping(ApiPath.POINT_CLAIM)
    ApiResponse<PointGrantResponse> claim(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointGrantRequest request
    ) {
        return ApiResponse.of(pointTransactionGrantUseCase.grant(userId, request));
    }

    @PostMapping(ApiPath.POINT_RECLAIM)
    ApiResponse<PointReclaimResponse> reclaim(
        @RequestHeader(ApiHeaders.USER_ID) final String userId,
        @RequestBody final PointReclaimRequest request
    ) {
        return ApiResponse.of(pointTransactionReclaimUseCase.reclaim(userId, request));
    }
}
