package im.dangmoo.benefit.api.web.point.controller;

import im.dangmoo.benefit.api.support.ApiResponse;
import im.dangmoo.benefit.api.support.UserHeaders;
import im.dangmoo.benefit.api.web.ApiPath;
import im.dangmoo.benefit.api.web.point.model.PointIssueRequest;
import im.dangmoo.benefit.api.web.point.model.PointRevokeRequest;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.api.web.point.service.PointIssueService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointIssueController {

    private final PointIssueService pointIssueService;

    public PointIssueController(final PointIssueService pointIssueService) {
        this.pointIssueService = pointIssueService;
    }

    @PostMapping(ApiPath.POINT_ISSUE)
    ApiResponse<PointTransactionResponse> issue(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestBody final PointIssueRequest request
    ) {
        return ApiResponse.of(pointIssueService.issue(userId, request));
    }

    @PostMapping(ApiPath.POINT_REVOKE)
    ApiResponse<PointTransactionResponse> revoke(
        @RequestHeader(UserHeaders.USER_ID) final String userId,
        @RequestBody final PointRevokeRequest request
    ) {
        return ApiResponse.of(pointIssueService.revoke(userId, request));
    }
}
