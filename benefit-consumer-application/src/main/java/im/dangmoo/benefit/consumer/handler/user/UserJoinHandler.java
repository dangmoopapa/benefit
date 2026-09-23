package im.dangmoo.benefit.consumer.handler.user;

import im.dangmoo.benefit.consumer.event.user.UserJoinEvent;
import im.dangmoo.benefit.domain.coupon.CouponExhaustionDomain;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.coupon.CouponUsageDomain;
import im.dangmoo.benefit.domain.coupon.CouponWalletDomain;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.domain.point.PointTransactionDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicy;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserJoinHandler {

    private static final String POINT_POLICY_KEY = "user.join";
    private static final String COUPON_POLICY_KEY = "user.join";
    private static final String CREATED_BY = "system";

    private final PointPolicyMongoRepository pointPolicyMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointBalanceMongoRepository pointBalanceMongoRepository;
    private final PointGrantStockRedisRepository pointGrantStockRedisRepository;
    private final CouponPolicyMongoRepository couponPolicyMongoRepository;
    private final CouponWalletMongoRepository couponWalletMongoRepository;
    private final CouponIssueStockRedisRepository couponIssueStockRedisRepository;
    private final CouponPolicyChangedPublisher couponPolicyChangedPublisher;

    public UserJoinHandler(
        final PointPolicyMongoRepository pointPolicyMongoRepository,
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointBalanceMongoRepository pointBalanceMongoRepository,
        final PointGrantStockRedisRepository pointGrantStockRedisRepository,
        final CouponPolicyMongoRepository couponPolicyMongoRepository,
        final CouponWalletMongoRepository couponWalletMongoRepository,
        final CouponIssueStockRedisRepository couponIssueStockRedisRepository,
        final CouponPolicyChangedPublisher couponPolicyChangedPublisher
    ) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
        this.pointGrantStockRedisRepository = pointGrantStockRedisRepository;
        this.couponPolicyMongoRepository = couponPolicyMongoRepository;
        this.couponWalletMongoRepository = couponWalletMongoRepository;
        this.couponIssueStockRedisRepository = couponIssueStockRedisRepository;
        this.couponPolicyChangedPublisher = couponPolicyChangedPublisher;
    }

    public void handle(final UserJoinEvent event) {
        final String userId = event.userId();
        grantPoint(userId);
        issueCoupon(userId);
    }

    private void grantPoint(final String userId) {
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

    private void issueCoupon(final String userId) {
        final CouponPolicy policy = couponPolicyMongoRepository.findByKey(COUPON_POLICY_KEY).orElse(null);
        if (policy == null || policy.getStatus().isNotActive()) {
            return;
        }

        final Instant now = Instant.now();
        final CouponIssueDomain issueDomain = CouponIssueDomain.of(policy.getIssueCondition());
        final CouponExhaustionDomain exhaustionDomain = CouponExhaustionDomain.of(policy.getIssueCondition());
        final String idempotencyKey = CouponWalletDomain.idempotencyKey(
            policy.getId(),
            userId,
            policy.getIssueCondition().getFrequency(),
            now
        );
        if (couponWalletMongoRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            return;
        }

        final long issuedCount = couponIssueStockRedisRepository.get(policy.getId());
        if (!issueDomain.isSatisfied(now, issuedCount)) {
            return;
        }

        final Instant expiresAt = CouponUsageDomain.of(
            policy.getUsageCondition(),
            policy.getApplyCondition()
        ).resolveExpiresAt(now);

        try {
            couponWalletMongoRepository.save(
                CouponWallet.create(
                    userId,
                    policy.getId(),
                    policy.getKey(),
                    idempotencyKey,
                    expiresAt,
                    CREATED_BY
                )
            );
        } catch (final DuplicateKeyException ignored) {
            return;
        }

        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.getId());
        if (exhaustionDomain.isJustExhausted(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedEvent.ofExhausted(policy));
        }
    }
}
