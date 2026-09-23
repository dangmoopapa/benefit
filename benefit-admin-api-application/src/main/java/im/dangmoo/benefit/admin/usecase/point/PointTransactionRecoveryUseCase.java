package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.dto.point.recovery.PointRecoveryRequest;
import im.dangmoo.benefit.admin.dto.point.recovery.PointRecoveryResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;

import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointBalanceMongoRepository;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
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

    public PointRecoveryResponse recover(final String adminId, final PointRecoveryRequest request) {
        final PointTransactionDocument use = pointTransactionMongoRepository
            .findByIdempotencyKey(PointTransactionDocument.useKey(request.orderId()))
            .orElseThrow(ApiException::notFound);
        if (!request.userId().equals(use.getUserId())) {
            throw ApiException.notFound();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransactionDocument.useCancel(
                use.getUserId(),
                use.getAmount(),
                request.orderId(),
                use.getId(),
                adminId
            )
        );
        if (!appended.created()) {
            return PointRecoveryResponse.of(appended.tx());
        }

        pointBalanceMongoRepository.increase(
            use.getUserId(),
            PointBalanceDocument.NEVER_EXPIRES_AT,
            use.getAmount()
        );
        return PointRecoveryResponse.of(appended.tx());
    }
}
