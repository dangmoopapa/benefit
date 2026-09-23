package im.dangmoo.benefit.admin.dto.coupon.wallet;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;

public record CouponWalletUsageResponse(String id) {

    public static CouponWalletUsageResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletUsageResponse(wallet.getId());
    }
}
