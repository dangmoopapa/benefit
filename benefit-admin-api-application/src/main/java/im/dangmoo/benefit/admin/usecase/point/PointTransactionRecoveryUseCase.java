package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.recovery.PointRecoveryRequest;
import im.dangmoo.benefit.admin.model.point.recovery.PointRecoveryResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointTransactionDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
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

    public PointRecoveryResponse execute(final String adminId, final PointRecoveryRequest request) {
        final PointTransaction use = pointTransactionMongoRepository
            .findByIdempotencyKey(PointTransactionDomain.useKey(request.orderId()))
            .orElseThrow(ApiException::notFound);
        if (!request.userId().equals(use.getUserId())) {
            throw ApiException.notFound();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.useCancel(
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
            PointBalance.NEVER_EXPIRES_AT,
            use.getAmount()
        );
        return PointRecoveryResponse.of(appended.tx());
    }
}
