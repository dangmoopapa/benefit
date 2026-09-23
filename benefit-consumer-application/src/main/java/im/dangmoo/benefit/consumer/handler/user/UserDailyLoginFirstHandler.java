package im.dangmoo.benefit.consumer.handler.user;

import im.dangmoo.benefit.consumer.event.user.UserDailyLoginFirstEvent;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.domain.point.PointTransactionDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
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

    public void handle(final UserDailyLoginFirstEvent event) {
        final String userId = event.userId();
        final PointPolicy policy = pointPolicyMongoRepository.findByKey(POINT_POLICY_KEY).orElse(null);
        if (policy == null || policy.getStatus().isNotActive()) {
            return;
        }

        final Instant now = Instant.now();
        if (!PointIssueDomain.of(policy.getIssueCondition()).isSatisfiedAt(now)) {
            return;
        }

        final String grantKey = PointTransactionDomain.grantKey(
            policy.getId(),
            userId,
            policy.getIssueCondition().getFrequency(),
            now
        );
        if (pointTransactionMongoRepository.findByIdempotencyKey(grantKey).isPresent()) {
            return;
        }

        final Instant expiresAt = PointExpireDomain.of(policy.getExpireCondition()).resolveExpiresAt(now);
        if (!PointExpireDomain.isNever(expiresAt) && !expiresAt.isAfter(now)) {
            return;
        }

        if (!pointGrantStockRedisRepository.tryReserve(
            policy.getId(),
            policy.getIssueCondition().getStockQuantity()
        )) {
            return;
        }

        final long amount = PointBenefitDomain.of(policy.getBenefitCondition()).resolveAmount();
        final var appended = pointTransactionMongoRepository.append(
            PointTransaction.grant(
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
