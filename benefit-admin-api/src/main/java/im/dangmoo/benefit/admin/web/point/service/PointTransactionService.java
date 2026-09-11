package im.dangmoo.benefit.admin.web.point.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.point.model.PointIssueRequest;
import im.dangmoo.benefit.admin.web.point.model.PointRestoreRequest;
import im.dangmoo.benefit.admin.web.point.model.PointRevokeRequest;
import im.dangmoo.benefit.admin.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.admin.web.point.model.PointUseRequest;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import im.dangmoo.benefit.domain.component.point.issue.PointIssue;
import im.dangmoo.benefit.domain.component.point.issue.PointIssuer;
import im.dangmoo.benefit.domain.component.point.issue.PointRevoke;
import im.dangmoo.benefit.domain.component.point.use.PointRedeemer;
import im.dangmoo.benefit.domain.component.point.use.PointRestore;
import im.dangmoo.benefit.domain.component.point.use.PointUse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointTransactionService {

    private final PointIssuer pointIssuer;
    private final PointRedeemer pointRedeemer;
    private final PointTransactionRepository pointTransactionRepository;

    public PointTransactionService(
        final PointIssuer pointIssuer,
        final PointRedeemer pointRedeemer,
        final PointTransactionRepository pointTransactionRepository
    ) {
        this.pointIssuer = pointIssuer;
        this.pointRedeemer = pointRedeemer;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public List<PointTransactionResponse> list(
        final String userId,
        final List<PointTransactionType> types
    ) {
        return pointTransactionRepository.findByUserId(userId, types).stream()
            .map(PointTransactionResponse::of)
            .toList();
    }

    public PointTransactionResponse issue(final String adminId, final PointIssueRequest request) {
        return switch (pointIssuer.issue(
            request.userId(),
            request.policyCode(),
            request.point(),
            request.orderId(),
            adminId
        )) {
            case PointIssue.Success success -> PointTransactionResponse.of(success);
            case PointIssue.PolicyNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointIssue.PolicyNotActive _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointIssue.InvalidAmount _ -> throw new ApiException(ApiMessage.INVALID_AMOUNT);
        };
    }

    public PointTransactionResponse revoke(final String adminId, final PointRevokeRequest request) {
        return switch (pointIssuer.revoke(
            request.relatedTransactionId(),
            request.orderId(),
            request.idempotencyKey(),
            null,
            adminId
        )) {
            case PointRevoke.Success success -> PointTransactionResponse.of(success);
            case PointRevoke.TransactionNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointRevoke.InvalidType _, PointRevoke.UserMismatch _ -> throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointRevoke.AlreadyRevoked _ -> throw new ApiException(ApiMessage.ALREADY_REVOKED);
            case PointRevoke.Insufficient _ -> throw new ApiException(ApiMessage.INSUFFICIENT_POINT);
        };
    }

    public PointTransactionResponse use(final String adminId, final PointUseRequest request) {
        return switch (pointRedeemer.use(
            request.userId(),
            request.point(),
            request.orderId(),
            request.idempotencyKey(),
            adminId
        )) {
            case PointUse.Success success -> PointTransactionResponse.of(success);
            case PointUse.Insufficient _ -> throw new ApiException(ApiMessage.INSUFFICIENT_POINT);
            case PointUse.InvalidAmount _ -> throw new ApiException(ApiMessage.INVALID_AMOUNT);
        };
    }

    public PointTransactionResponse restore(final String adminId, final PointRestoreRequest request) {
        return switch (pointRedeemer.restore(
            request.relatedTransactionId(),
            request.orderId(),
            request.idempotencyKey(),
            null,
            adminId
        )) {
            case PointRestore.Success success -> PointTransactionResponse.of(success);
            case PointRestore.TransactionNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointRestore.InvalidType _, PointRestore.UserMismatch _ ->
                throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointRestore.AlreadyRestored _ -> throw new ApiException(ApiMessage.ALREADY_RESTORED);
        };
    }
}
