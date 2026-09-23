package im.dangmoo.benefit.api.dto.coupon;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;

public record CouponUsageResponse(String walletId) {

    public static CouponUsageResponse of(final CouponWalletDocument wallet) {
        return new CouponUsageResponse(wallet.getId());
    }
}
