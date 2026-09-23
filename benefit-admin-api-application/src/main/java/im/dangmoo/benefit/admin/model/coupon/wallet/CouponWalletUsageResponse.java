package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;

public record CouponWalletUsageResponse(String id) {

    public static CouponWalletUsageResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletUsageResponse(wallet.getId());
    }
}
