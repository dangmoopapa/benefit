package im.dangmoo.benefit.admin.model.coupon.wallet;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWallet;

public record CouponWalletRecoveryResponse(String id) {

    public static CouponWalletRecoveryResponse of(final CouponWallet wallet) {
        return new CouponWalletRecoveryResponse(wallet.getId());
    }
}
