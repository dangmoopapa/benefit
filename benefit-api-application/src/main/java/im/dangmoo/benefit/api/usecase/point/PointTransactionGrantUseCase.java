package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointGrantRequest;
import im.dangmoo.benefit.api.model.point.PointGrantResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PointTransactionGrantUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;
    private final PointGrantStockRedisRepository pointGrantStockRedisRepository;

    public PointTransactionGrantUseCase(
        final PointPolicyMongoRepository pointPolicyMongoRepository,
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository,
        final PointGrantStockRedisRepository pointGrantStockRedisRepository
    ) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
        this.pointGrantStockRedisRepository = pointGrantStockRedisRepository;
    }

    public PointGrantResponse execute(final String userId, final PointGrantRequest request) {
        final PointPolicy policy = pointPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);
        if (policy.getStatus().isNotActive()) {
            throw ApiException.invalidStatus();
        }

        final Instant now = Instant.now();
        if (!PointIssueDomain.of(policy.getIssueCondition()).isSatisfiedAt(now)) {
            throw ApiException.conditionNotSatisfied();
        }

        final var existing = pointTransactionMongoRepository.findByIdempotencyKey(
            PointTransaction.grantKey(policy.getId(), userId)
        );
        if (existing.isPresent()) {
            return PointGrantResponse.of(existing.get());
        }

        final Instant expiresAt = PointExpireDomain.of(policy.getExpireCondition()).resolveExpiresAt(now);
        if (!PointExpireDomain.isNever(expiresAt) && !expiresAt.isAfter(now)) {
            throw ApiException.conditionNotSatisfied();
        }

        if (!pointGrantStockRedisRepository.tryReserve(
            policy.getId(),
            policy.getIssueCondition().getStockQuantity()
        )) {
            throw ApiException.stockExhausted();
        }

        final long amount = policy.getBenefitCondition().getAmount();
        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.grant(userId, policy.getId(), policy.getKey(), amount, expiresAt, userId)
        );
        if (!appended.created()) {
            pointGrantStockRedisRepository.release(policy.getId());
            return PointGrantResponse.of(appended.tx());
        }

        pointBalanceMongoRepository.increase(userId, expiresAt, amount);
        return PointGrantResponse.of(appended.tx());
    }
}
