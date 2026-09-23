package im.dangmoo.benefit.admin.dto.coupon.wallet;

import im.dangmoo.benefit.data.entity.coupon.wallet.CouponWalletDocument;

public record CouponWalletRecoveryResponse(String id) {

    public static CouponWalletRecoveryResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletRecoveryResponse(wallet.getId());
    }
}
