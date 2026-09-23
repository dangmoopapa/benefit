package im.dangmoo.benefit.consumer.handler;

import im.dangmoo.benefit.consumer.consumption.UserDailyLoginFirstConsumption;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.data.infrastructure.point.PointBalanceMongoRepository;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.point.PointGrantStockRedisRepository;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserDailyLoginFirstHandler {

    private static final String POINT_POLICY_KEY = "user.daily.login.first";
    private static final String CREATED_BY = "system";

    private final PointPolicyMongoRepository pointPolicyMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;
    private final PointGrantStockRedisRepository pointGrantStockRedisRepository;

    public UserDailyLoginFirstHandler(
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

    public void handle(final UserDailyLoginFirstConsumption consumption) {
        grantPoint(consumption.userId());
    }

    private void grantPoint(final String userId) {
        final PointPolicyDocument policy = pointPolicyMongoRepository.findByKey(POINT_POLICY_KEY).orElse(null);
        if (policy == null) {
            return;
        }

        final Instant now = Instant.now();
        final PointIssueDomain pointIssue = PointIssueDomain.of(policy);
        final String grantKey = pointIssue.grantKeyFor(policy.getId(), userId, now);
        if (pointTransactionMongoRepository.findByIdempotencyKey(grantKey).isPresent()) {
            return;
        }

        final PointExpireDomain pointExpire = PointExpireDomain.of(policy, now);
        if (pointExpire.isExpiredAt(now)) {
            return;
        }

        final long grantedCount = pointGrantStockRedisRepository.get(policy.getId());
        if (pointIssue.issuabilityAt(now, false, grantedCount) != PointIssueDomain.Issuability.ISSUABLE) {
            return;
        }

        if (!pointGrantStockRedisRepository.tryReserve(policy.getId(), pointIssue.stockQuantity())) {
            return;
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
                CREATED_BY
            )
        );
        if (!appended.created()) {
            pointGrantStockRedisRepository.release(policy.getId());
            return;
        }

        pointBalanceMongoRepository.increase(userId, expiresAt, amount);
    }
}
