package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;

public record CouponWalletUsageResponse(String id) {

    public static CouponWalletUsageResponse of(final CouponWallet wallet) {
        return new CouponWalletUsageResponse(wallet.getId());
    }
}
