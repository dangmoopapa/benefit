package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWalletStatus;
import im.dangmoo.benefit.domain.function.coupon.issue.CouponIssue;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponRecover;
import im.dangmoo.benefit.domain.function.coupon.redeem.CouponUse;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponWalletResponse(
    String id,
    String userId,
    String orderId,
    String policyId,
    String policyCode,
    CouponWalletStatus status,
    BigDecimal usedAmount,
    Instant issuedAt,
    Instant expiresAt,
    Instant usedAt,
    Instant recoveredAt,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static CouponWalletResponse of(final CouponWallet document) {
        return new CouponWalletResponse(
            document.getId(),
            document.getUserId(),
            document.getOrderId(),
            document.getPolicyId(),
            document.getPolicyCode(),
            document.getStatus(),
            document.getUsedAmount(),
            document.getIssuedAt(),
            document.getExpiresAt(),
            document.getUsedAt(),
            document.getRecoveredAt(),
            document.getCreatedBy(),
            document.getCreatedAt(),
            document.getUpdatedBy(),
            document.getUpdatedAt()
        );
    }

    public static CouponWalletResponse of(final CouponIssue.Success issued) {
        return new CouponWalletResponse(
            issued.walletId(),
            issued.userId(),
            null,
            issued.policyId(),
            issued.policyCode(),
            issued.status(),
            null,
            issued.issuedAt(),
            issued.expiresAt(),
            null,
            null,
            issued.createdBy(),
            issued.createdAt(),
            issued.updatedBy(),
            issued.updatedAt()
        );
    }

    public static CouponWalletResponse of(final CouponUse.Success used) {
        return new CouponWalletResponse(
            used.walletId(),
            used.userId(),
            used.orderId(),
            used.policyId(),
            used.policyCode(),
            used.status(),
            used.usedAmount(),
            null,
            used.expiresAt(),
            used.usedAt(),
            null,
            used.createdBy(),
            used.createdAt(),
            used.updatedBy(),
            used.updatedAt()
        );
    }

    public static CouponWalletResponse of(final CouponRecover.Success recovered) {
        return new CouponWalletResponse(
            recovered.walletId(),
            recovered.userId(),
            recovered.orderId(),
            recovered.policyId(),
            recovered.policyCode(),
            recovered.status(),
            null,
            null,
            recovered.expiresAt(),
            null,
            recovered.recoveredAt(),
            recovered.createdBy(),
            recovered.createdAt(),
            recovered.updatedBy(),
            recovered.updatedAt()
        );
    }
}
