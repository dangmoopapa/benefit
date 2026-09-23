package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CouponWalletSearchResponse(List<Item> items) {

    public static CouponWalletSearchResponse of(final List<CouponWalletDocument> wallets) {
        return new CouponWalletSearchResponse(wallets.stream().map(Item::of).toList());
    }

    public record Item(
        String id,
        String userId,
        String orderId,
        String policyId,
        String policyKey,
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

        public static Item of(final CouponWalletDocument wallet) {
            return new Item(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getOrderId(),
                wallet.getPolicyId(),
                wallet.getPolicyKey(),
                wallet.getStatus(),
                wallet.getUsedAmount(),
                wallet.getIssuedAt(),
                wallet.getExpiresAt(),
                wallet.getUsedAt(),
                wallet.getRecoveredAt(),
                wallet.getCreatedBy(),
                wallet.getCreatedAt(),
                wallet.getUpdatedBy(),
                wallet.getUpdatedAt()
            );
        }
    }
}
