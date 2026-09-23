package im.dangmoo.benefit.consumer.consumption;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;

import java.time.Instant;

public record CouponTimeAttackIssueConsumption(
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

    public CouponWalletDocument toWallet() {
        return CouponWalletDocument.of(
            walletId,
            userId,
            policyId,
            policyKey,
            idempotencyKey,
            status,
            issuedAt,
            expiresAt,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt
        );
    }
}
