package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;

public record CouponRecoveryResponse(String walletId) {

    public static CouponRecoveryResponse of(final CouponWallet wallet) {
        return new CouponRecoveryResponse(wallet.getId());
    }
}
