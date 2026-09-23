package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointGrantRequest;
import im.dangmoo.benefit.api.model.point.PointGrantResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;
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

    public PointGrantResponse grant(final String userId, final PointGrantRequest request) {
        final PointPolicyDocument policy = pointPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);

        final Instant now = Instant.now();
        final PointIssueDomain pointIssue = PointIssueDomain.of(policy);
        final String grantKey = pointIssue.grantKeyFor(policy.getId(), userId, now);
        final var existing = pointTransactionMongoRepository.findByIdempotencyKey(grantKey);
        if (existing.isPresent()) {
            return PointGrantResponse.of(existing.get());
        }

        final PointExpireDomain pointExpire = PointExpireDomain.of(policy, now);
        if (pointExpire.isExpiredAt(now)) {
            throw ApiException.conditionNotSatisfied();
        }

        final long grantedCount = pointGrantStockRedisRepository.get(policy.getId());
        switch (pointIssue.issuabilityAt(now, false, grantedCount)) {
            case POLICY_INACTIVE -> throw ApiException.invalidStatus();
            case OUT_OF_PERIOD -> throw ApiException.conditionNotSatisfied();
            case STOCK_EXHAUSTED -> throw ApiException.stockExhausted();
            case ALREADY_GRANTED, ISSUABLE -> {
            }
        }

        if (!pointGrantStockRedisRepository.tryReserve(policy.getId(), pointIssue.stockQuantity())) {
            throw ApiException.stockExhausted();
        }

        final Instant expiresAt = pointExpire.expiresAt();
        final long amount = PointBenefitDomain.of(policy).grantAmount();
        final var appended = pointTransactionMongoRepository.append(
            PointTransactionDocument.grant(
                userId,
                policy.getId(),
                policy.getKey(),
                amount,
                expiresAt,
                grantKey,
                userId
            )
        );
        if (!appended.created()) {
            pointGrantStockRedisRepository.release(policy.getId());
            return PointGrantResponse.of(appended.tx());
        }

        pointBalanceMongoRepository.increase(userId, expiresAt, amount);
        return PointGrantResponse.of(appended.tx());
    }
}
