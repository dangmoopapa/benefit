package im.dangmoo.benefit.infrastructure.data.coupon.wallet;

import java.time.Instant;

public record CouponWalletIssueEvent(
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

    public static CouponWalletIssueEvent of(final CouponWallet wallet) {
        return new CouponWalletIssueEvent(
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

    public CouponWallet toWallet() {
        return CouponWallet.of(
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
