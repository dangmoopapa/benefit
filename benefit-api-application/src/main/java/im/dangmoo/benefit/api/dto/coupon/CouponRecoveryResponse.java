package im.dangmoo.benefit.api.dto.coupon;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;

public record CouponRecoveryResponse(String walletId) {

    public static CouponRecoveryResponse of(final CouponWalletDocument wallet) {
        return new CouponRecoveryResponse(wallet.getId());
    }
}
