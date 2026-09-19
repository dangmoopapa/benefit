package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletStatus;

import java.time.Instant;
import java.util.List;

public record CouponBoxResponse(
    List<Item> available,
    List<Item> unavailable
) {

    public record Item(
        String walletId,
        String policyId,
        String policyKey,
        String policyName,
        CouponWalletStatus status,
        Instant issuedAt,
        Instant expiresAt,
        Instant usedAt
    ) {

        public static Item of(final CouponWallet wallet, final String policyName) {
            return new Item(
                wallet.getId(),
                wallet.getPolicyId(),
                wallet.getPolicyKey(),
                policyName,
                wallet.getStatus(),
                wallet.getIssuedAt(),
                wallet.getExpiresAt(),
                wallet.getUsedAt()
            );
        }
    }
}
