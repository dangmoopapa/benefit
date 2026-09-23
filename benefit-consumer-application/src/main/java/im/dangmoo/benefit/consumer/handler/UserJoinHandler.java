package im.dangmoo.benefit.consumer.handler;

import im.dangmoo.benefit.consumer.consumption.UserJoinConsumption;
import im.dangmoo.benefit.domain.coupon.CouponIssueDomain;
import im.dangmoo.benefit.domain.point.PointBenefitDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublication;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.changed.CouponPolicyChangedPublisher;
import im.dangmoo.benefit.infrastructure.data.coupon.policy.CouponPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.stock.CouponIssueStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionDocument;
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

    public void handle(final UserJoinConsumption consumption) {
        final String userId = consumption.userId();
        grantPoint(userId);
        issueCoupon(userId);
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

    private void issueCoupon(final String userId) {
        final CouponPolicyDocument policy = couponPolicyMongoRepository.findByKey(COUPON_POLICY_KEY).orElse(null);
        if (policy == null) {
            return;
        }

        final Instant now = Instant.now();
        final CouponIssueDomain couponIssue = CouponIssueDomain.of(policy);
        final String issueKey = couponIssue.issueKeyFor(policy.getId(), userId, now);
        final boolean alreadyIssued = couponWalletMongoRepository.findByIdempotencyKey(issueKey).isPresent();
        final long issuedCount = couponIssueStockRedisRepository.get(policy.getId());
        if (couponIssue.issuabilityAt(now, alreadyIssued, issuedCount) != CouponIssueDomain.Issuability.ISSUABLE) {
            return;
        }

        try {
            couponWalletMongoRepository.save(
                CouponWalletDocument.create(
                    userId,
                    policy.getId(),
                    policy.getKey(),
                    issueKey,
                    couponIssue.expiresAtFrom(now),
                    CREATED_BY
                )
            );
        } catch (final DuplicateKeyException ignored) {
            return;
        }

        final long issuedCountAfter = couponIssueStockRedisRepository.increment(policy.getId());
        if (couponIssue.isLastIssue(issuedCountAfter)) {
            couponPolicyChangedPublisher.publish(CouponPolicyChangedPublication.ofExhausted(policy));
        }
    }
}
