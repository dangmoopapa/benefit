package im.dangmoo.benefit.domain.function.coupon.redeem;

import im.dangmoo.benefit.domain.data.coupon.wallet.CouponWallet;

public record CouponUseResult(
    CouponUseReason reason,
    CouponWallet wallet
) {

    public static CouponUseResult used(final CouponWallet wallet) {
        return new CouponUseResult(CouponUseReason.USED, wallet);
    }

    public static CouponUseResult of(final CouponUseReason reason) {
        return new CouponUseResult(reason, null);
    }
}
