package im.dangmoo.benefit.data.entity.coupon.wallet.issue;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;

import java.time.Instant;

public record CouponTimaAttackIssuePublication(
    String walletId,
    String userId,
    String policyId,
    String policyKey,
    String idempotencyKey,
    CouponWalletStatus status,
    Instant issuedAt,
    Instant expiresAt,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponTimaAttackIssuePublication of(final CouponWalletDocument wallet) {
        return new CouponTimaAttackIssuePublication(
            wallet.getId(),
            wallet.getUserId(),
            wallet.getPolicyId(),
            wallet.getPolicyKey(),
            wallet.getIdempotencyKey(),
            wallet.getStatus(),
            wallet.getIssuedAt(),
            wallet.getExpiresAt(),
            wallet.getCreatedBy(),
            wallet.getCreatedAt(),
            wallet.getUpdatedBy(),
            wallet.getUpdatedAt()
        );
    }
}
