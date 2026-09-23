package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.dto.point.PointReclaimRequest;
import im.dangmoo.benefit.api.dto.point.PointReclaimResponse;
import im.dangmoo.benefit.api.usecase.ApiException;
import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointBalanceMongoRepository;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
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

    public PointReclaimResponse reclaim(final String userId, final PointReclaimRequest request) {
        if (!StringUtils.hasText(request.idempotencyKey())) {
            throw ApiException.conditionNotSatisfied();
        }

        final PointPolicyDocument policy = pointPolicyMongoRepository.findByKey(request.policyKey())
            .orElseThrow(ApiException::notFound);

        final Instant now = Instant.now();
        final PointBalanceDocument balance = pointBalanceMongoRepository.findByUserId(userId)
            .orElseThrow(ApiException::notFound);

        final long availableAmount = PointBalanceDomain.of(balance).availableAmountAt(now);
        final long amount = request.amount() == null ? availableAmount : request.amount();
        switch (PointIssueDomain.of(policy).reclaimabilityOf(amount, availableAmount)) {
            case POLICY_NOT_RECLAIMABLE -> throw ApiException.conditionNotSatisfied();
            case INVALID_AMOUNT, INSUFFICIENT_BALANCE -> throw ApiException.insufficientPoint();
            case RECLAIMABLE -> {
            }
        }

        final var appended = pointTransactionMongoRepository.append(
            PointTransactionDocument.reclaim(
                userId,
                policy.getId(),
                policy.getKey(),
                amount,
                request.idempotencyKey(),
                userId
            )
        );
        if (!appended.created()) {
            return PointReclaimResponse.of(appended.tx());
        }

        try {
            pointBalanceMongoRepository.consume(userId, amount, now);
        } catch (final RuntimeException ex) {
            throw ApiException.insufficientPoint();
        }
        return PointReclaimResponse.of(appended.tx());
    }
}
