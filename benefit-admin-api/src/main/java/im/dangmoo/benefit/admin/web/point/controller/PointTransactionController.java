package im.dangmoo.benefit.admin.web.point.controller;

import im.dangmoo.benefit.admin.support.AdminHeaders;
import im.dangmoo.benefit.admin.support.ApiResponse;
import im.dangmoo.benefit.admin.web.AdminApiPath;
import im.dangmoo.benefit.admin.web.point.model.PointIssueRequest;
import im.dangmoo.benefit.admin.web.point.model.PointRestoreRequest;
import im.dangmoo.benefit.admin.web.point.model.PointRevokeRequest;
import im.dangmoo.benefit.admin.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.admin.web.point.model.PointUseRequest;
import im.dangmoo.benefit.admin.web.point.service.PointTransactionService;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PointTransactionController {

    private final PointTransactionService pointTransactionService;

    public PointTransactionController(final PointTransactionService pointTransactionService) {
        this.pointTransactionService = pointTransactionService;
    }

    @GetMapping(AdminApiPath.POINT_TRANSACTIONS)
    ApiResponse<List<PointTransactionResponse>> list(
        @RequestParam final String userId,
        @RequestParam(required = false) final List<PointTransactionType> types
    ) {
        return ApiResponse.of(pointTransactionService.list(userId, types));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_ISSUE)
    ApiResponse<PointTransactionResponse> issue(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointIssueRequest request
    ) {
        return ApiResponse.of(pointTransactionService.issue(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_REVOKE)
    ApiResponse<PointTransactionResponse> revoke(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointRevokeRequest request
    ) {
        return ApiResponse.of(pointTransactionService.revoke(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_USE)
    ApiResponse<PointTransactionResponse> use(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointUseRequest request
    ) {
        return ApiResponse.of(pointTransactionService.use(adminId, request));
    }

    @PostMapping(AdminApiPath.POINT_TRANSACTION_RESTORE)
    ApiResponse<PointTransactionResponse> restore(
        @RequestHeader(AdminHeaders.ADMIN_ID) final String adminId,
        @RequestBody final PointRestoreRequest request
    ) {
        return ApiResponse.of(pointTransactionService.restore(adminId, request));
    }
}
