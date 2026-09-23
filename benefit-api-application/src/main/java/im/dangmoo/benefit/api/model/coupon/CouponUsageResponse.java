package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;

public record CouponUsageResponse(String walletId) {

    public static CouponUsageResponse of(final CouponWalletDocument wallet) {
        return new CouponUsageResponse(wallet.getId());
    }
}
