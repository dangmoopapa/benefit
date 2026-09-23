package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointRecoveryRequest;
import im.dangmoo.benefit.api.model.point.PointRecoveryResponse;
import im.dangmoo.benefit.api.usecase.ApiException;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class PointTransactionRecoveryUseCase {

    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;

    public PointTransactionRecoveryUseCase(
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository
    ) {
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
    }

    public PointRecoveryResponse recover(final String userId, final PointRecoveryRequest request) {
        final PointTransactionDocument use = pointTransactionMongoRepository
            .findByIdempotencyKey(PointTransactionDocument.useKey(request.orderId()))
            .orElseThrow(ApiException::notFound);
        if (!userId.equals(use.getUserId())) {
            throw ApiException.notFound();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransactionDocument.useCancel(
                userId,
                use.getAmount(),
                request.orderId(),
                use.getId(),
                userId
            )
        );
        if (!appended.created()) {
            return PointRecoveryResponse.of(appended.tx());
        }

        pointBalanceMongoRepository.increase(
            userId,
            PointBalanceDocument.NEVER_EXPIRES_AT,
            use.getAmount()
        );
        return PointRecoveryResponse.of(appended.tx());
    }
}
