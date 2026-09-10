package im.dangmoo.benefit.api.web.point.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.point.model.PointIssueRequest;
import im.dangmoo.benefit.api.web.point.model.PointRevokeRequest;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.domain.function.point.issue.PointIssue;
import im.dangmoo.benefit.domain.function.point.issue.PointIssuer;
import im.dangmoo.benefit.domain.function.point.issue.PointRevoke;
import org.springframework.stereotype.Service;

@Service
public class PointIssueService {

    private final PointIssuer pointIssuer;

    public PointIssueService(final PointIssuer pointIssuer) {
        this.pointIssuer = pointIssuer;
    }

    public PointTransactionResponse issue(final String userId, final PointIssueRequest request) {
        return switch (pointIssuer.issue(
            userId,
            request.policyCode(),
            request.point(),
            request.orderId(),
            request.idempotencyKey(),
            userId
        )) {
            case PointIssue.Success success -> PointTransactionResponse.of(success);
            case PointIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointIssue.InvalidAmount _ -> throw new ApiException(ApiMessage.INVALID_AMOUNT);
        };
    }

    public PointTransactionResponse revoke(final String userId, final PointRevokeRequest request) {
        return switch (pointIssuer.revoke(
            request.relatedTransactionId(),
            request.orderId(),
            request.idempotencyKey(),
            userId,
            userId
        )) {
            case PointRevoke.Success success -> PointTransactionResponse.of(success);
            case PointRevoke.TransactionNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointRevoke.InvalidType _, PointRevoke.UserMismatch _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointRevoke.AlreadyRevoked _ -> throw new ApiException(ApiMessage.ALREADY_REVOKED);
            case PointRevoke.Insufficient _ -> throw new ApiException(ApiMessage.INSUFFICIENT_POINT);
        };
    }
}
