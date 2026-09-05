package im.dangmoo.benefit.admin.web.coupon.model;

import im.dangmoo.benefit.domain.coupon.document.wallet.CouponWallet;
import im.dangmoo.benefit.domain.coupon.document.wallet.CouponWalletStatus;

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
}
