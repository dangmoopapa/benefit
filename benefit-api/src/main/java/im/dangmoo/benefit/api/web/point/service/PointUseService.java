package im.dangmoo.benefit.api.web.point.service;

import im.dangmoo.benefit.api.support.ApiException;
import im.dangmoo.benefit.api.support.ApiMessage;
import im.dangmoo.benefit.api.web.point.model.PointRestoreRequest;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.api.web.point.model.PointUseRequest;
import im.dangmoo.benefit.domain.function.point.use.PointRedeemer;
import im.dangmoo.benefit.domain.function.point.use.PointRestore;
import im.dangmoo.benefit.domain.function.point.use.PointUse;
import org.springframework.stereotype.Service;

@Service
public class PointUseService {

    private final PointRedeemer pointRedeemer;

    public PointUseService(final PointRedeemer pointRedeemer) {
        this.pointRedeemer = pointRedeemer;
    }

    public PointTransactionResponse use(final String userId, final PointUseRequest request) {
        return switch (pointRedeemer.use(
            userId,
            request.point(),
            request.orderId(),
            request.idempotencyKey(),
            userId
        )) {
            case PointUse.Success success -> PointTransactionResponse.of(success);
            case PointUse.Insufficient _ -> throw new ApiException(ApiMessage.INSUFFICIENT_POINT);
            case PointUse.InvalidAmount _ -> throw new ApiException(ApiMessage.INVALID_AMOUNT);
        };
    }

    public PointTransactionResponse restore(final String userId, final PointRestoreRequest request) {
        return switch (pointRedeemer.restore(
            request.relatedTransactionId(),
            request.orderId(),
            request.idempotencyKey(),
            userId,
            userId
        )) {
            case PointRestore.Success success -> PointTransactionResponse.of(success);
            case PointRestore.TransactionNotFound _ -> throw new ApiException(ApiMessage.NOT_FOUND);
            case PointRestore.InvalidType _, PointRestore.UserMismatch _ ->
                throw new ApiException(ApiMessage.INVALID_STATUS);
            case PointRestore.AlreadyRestored _ -> throw new ApiException(ApiMessage.ALREADY_RESTORED);
        };
    }
}
