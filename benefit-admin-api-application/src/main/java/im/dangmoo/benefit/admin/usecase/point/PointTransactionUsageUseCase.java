package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.usage.PointUsageRequest;
import im.dangmoo.benefit.admin.model.point.usage.PointUsageResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
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

    public PointUsageResponse execute(final String adminId, final PointUsageRequest request) {
        final Instant now = Instant.now();
        final PointBalance balance = pointBalanceMongoRepository.findByUserId(request.userId())
            .orElseThrow(ApiException::insufficientPoint);
        if (PointBalanceDomain.of(balance).availableAmount(now) < request.amount()) {
            throw ApiException.insufficientPoint();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.use(request.userId(), request.amount(), request.orderId(), adminId)
        );
        if (!appended.created()) {
            return PointUsageResponse.of(appended.tx());
        }

        try {
            pointBalanceMongoRepository.consume(request.userId(), request.amount(), now);
        } catch (final RuntimeException ex) {
            throw ApiException.insufficientPoint();
        }
        return PointUsageResponse.of(appended.tx());
    }
}
