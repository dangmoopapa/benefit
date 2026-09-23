package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletDocument;

public record CouponWalletRecoveryResponse(String id) {

    public static CouponWalletRecoveryResponse of(final CouponWalletDocument wallet) {
        return new CouponWalletRecoveryResponse(wallet.getId());
    }
}
