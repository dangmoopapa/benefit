package im.dangmoo.benefit.api.dto.coupon;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletStatus;

import java.time.Instant;

public record CouponIssueResponse(
    String walletId,
    String policyId,
    String policyKey,
    CouponWalletStatus status,
    Instant issuedAt,
    Instant expiresAt
) {

    public static CouponIssueResponse of(final CouponWalletDocument wallet) {
        return new CouponIssueResponse(
            wallet.getId(),
            wallet.getPolicyId(),
            wallet.getPolicyKey(),
            wallet.getStatus(),
            wallet.getIssuedAt(),
            wallet.getExpiresAt()
        );
    }
}
