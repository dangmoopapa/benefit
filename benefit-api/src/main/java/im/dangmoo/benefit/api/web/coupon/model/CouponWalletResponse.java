package im.dangmoo.benefit.api.web.coupon.model;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletStatus;
import im.dangmoo.benefit.domain.component.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.component.coupon.redeem.CouponRecover;
import im.dangmoo.benefit.domain.component.coupon.redeem.CouponUse;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponWalletResponse(
    String id,
    String userId,
    String orderId,
    String policyId,
    String policyCode,
    String idempotencyKey,
    CouponWalletStatus status,
    BigDecimal usedAmount,
    Instant issuedAt,
    Instant expiresAt,
    Instant usedAt,
    Instant recoveredAt
) {

    public static CouponWalletResponse of(final CouponWallet entity) {
        return new CouponWalletResponse(
            entity.getId(),
            entity.getUserId(),
            entity.getOrderId(),
            entity.getPolicyId(),
            entity.getPolicyCode(),
            entity.getIdempotencyKey(),
            entity.getStatus(),
            entity.getUsedAmount(),
            entity.getIssuedAt(),
            entity.getExpiresAt(),
            entity.getUsedAt(),
            entity.getRecoveredAt()
        );
    }

    public static CouponWalletResponse of(final CouponIssue.Success issued) {
        return new CouponWalletResponse(
            issued.walletId(),
            issued.userId(),
            null,
            issued.policyId(),
            issued.policyCode(),
            issued.idempotencyKey(),
            issued.status(),
            null,
            issued.issuedAt(),
            issued.expiresAt(),
            null,
            null
        );
    }

    public static CouponWalletResponse of(final CouponUse.Success used) {
        return new CouponWalletResponse(
            used.walletId(),
            used.userId(),
            used.orderId(),
            used.policyId(),
            used.policyCode(),
            used.idempotencyKey(),
            used.status(),
            used.usedAmount(),
            null,
            used.expiresAt(),
            used.usedAt(),
            null
        );
    }

    public static CouponWalletResponse of(final CouponRecover.Success recovered) {
        return new CouponWalletResponse(
            recovered.walletId(),
            recovered.userId(),
            recovered.orderId(),
            recovered.policyId(),
            recovered.policyCode(),
            recovered.idempotencyKey(),
            recovered.status(),
            null,
            null,
            recovered.expiresAt(),
            null,
            recovered.recoveredAt()
        );
    }
}
