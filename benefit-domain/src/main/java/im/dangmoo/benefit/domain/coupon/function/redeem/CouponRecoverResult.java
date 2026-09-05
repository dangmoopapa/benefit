package im.dangmoo.benefit.domain.coupon.function.redeem;

import im.dangmoo.benefit.domain.coupon.data.wallet.CouponWallet;

public record CouponRecoverResult(
    CouponRecoverReason reason,
    CouponWallet wallet
) {

    public static CouponRecoverResult recovered(final CouponWallet wallet) {
        return new CouponRecoverResult(CouponRecoverReason.RECOVERED, wallet);
    }

    public static CouponRecoverResult of(final CouponRecoverReason reason) {
        return new CouponRecoverResult(reason, null);
    }
}
