package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;

public record CouponRecoveryResponse(String walletId) {

    public static CouponRecoveryResponse of(final CouponWalletDocument wallet) {
        return new CouponRecoveryResponse(wallet.getId());
    }
}
