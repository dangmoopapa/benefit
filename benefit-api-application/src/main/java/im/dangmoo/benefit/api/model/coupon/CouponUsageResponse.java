package im.dangmoo.benefit.api.model.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;

public record CouponUsageResponse(String walletId) {

    public static CouponUsageResponse of(final CouponWallet wallet) {
        return new CouponUsageResponse(wallet.getId());
    }
}
