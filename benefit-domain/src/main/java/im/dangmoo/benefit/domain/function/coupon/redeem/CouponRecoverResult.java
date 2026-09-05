package im.dangmoo.benefit.domain.function.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;

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
