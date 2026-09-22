package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.model.point.reclaim.PointReclaimRequest;
import im.dangmoo.benefit.admin.model.point.reclaim.PointReclaimResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.domain.point.PointRecoveryDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class PointTransactionReclaimUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;

    public PointTransactionReclaimUseCase(
        final PointPolicyMongoRepository pointPolicyMongoRepository,
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository
    ) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
    }

    public PointReclaimResponse reclaim(final String adminId, final PointReclaimRequest request) {
        if (!StringUtils.hasText(request.idempotencyKey())) {
            throw ApiException.conditionNotSatisfied();
        }

        final PointPolicy policy = pointPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);
        if (!PointRecoveryDomain.of(policy.getLifecycleCondition()).isReclaimable()) {
            throw ApiException.conditionNotSatisfied();
        }

        final Instant now = Instant.now();
        final PointBalance balance = pointBalanceMongoRepository.findByUserId(request.userId())
            .orElseThrow(ApiException::notFound);

        final long available = PointBalanceDomain.of(balance).availableAmount(now);
        final long amount = request.amount() == null ? available : request.amount();
        if (amount <= 0 || amount > available) {
            throw ApiException.insufficientPoint();
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.reclaim(
                request.userId(),
                policy.getId(),
                policy.getKey(),
                amount,
                request.idempotencyKey(),
                adminId
            )
        );
        if (!appended.created()) {
            return PointReclaimResponse.of(appended.tx());
        }

        try {
            pointBalanceMongoRepository.consume(request.userId(), amount, now);
        } catch (final RuntimeException ex) {
            throw ApiException.insufficientPoint();
        }
        return PointReclaimResponse.of(appended.tx());
    }
}
