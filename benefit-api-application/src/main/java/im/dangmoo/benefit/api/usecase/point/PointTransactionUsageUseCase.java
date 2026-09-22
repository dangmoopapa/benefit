package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointUsageRequest;
import im.dangmoo.benefit.api.model.point.PointUsageResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PointTransactionUsageUseCase {

    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;

    public PointTransactionUsageUseCase(
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository
    ) {
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
    }

    public PointUsageResponse use(final String userId, final PointUsageRequest request) {
        final Instant now = Instant.now();
        final PointBalance balance = pointBalanceMongoRepository.findByUserId(userId)
            .orElseThrow(ApiException::insufficientPoint);
        if (PointBalanceDomain.of(balance).availableAmount(now) < request.amount()) {
            throw ApiException.insufficientPoint();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.use(userId, request.amount(), request.orderId(), userId)
        );
        if (!appended.created()) {
            return PointUsageResponse.of(appended.tx());
        }

        try {
            pointBalanceMongoRepository.consume(userId, request.amount(), now);
        } catch (final RuntimeException ex) {
            throw ApiException.insufficientPoint();
        }
        return PointUsageResponse.of(appended.tx());
    }
}
