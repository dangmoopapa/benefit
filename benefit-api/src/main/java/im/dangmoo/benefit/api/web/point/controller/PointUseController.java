package im.dangmoo.benefit.api.web.point.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.point.model.PointRestoreRequest;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.api.web.point.model.PointUseRequest;
import im.dangmoo.benefit.api.web.point.service.PointUseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointUseController {

    private final PointUseService pointUseService;

    public PointUseController(final PointUseService pointUseService) {
        this.pointUseService = pointUseService;
    }

    @PostMapping(ApiPath.POINT_USE)
    ApiResponse<PointTransactionResponse> use(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestBody final PointUseRequest request
    ) {
        return ApiResponse.of(pointUseService.use(userId, request));
    }

    @PostMapping(ApiPath.POINT_RESTORE)
    ApiResponse<PointTransactionResponse> restore(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestBody final PointRestoreRequest request
    ) {
        return ApiResponse.of(pointUseService.restore(userId, request));
    }
}
