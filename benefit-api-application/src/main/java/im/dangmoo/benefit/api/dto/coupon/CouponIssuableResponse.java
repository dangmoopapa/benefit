package im.dangmoo.benefit.api.dto.coupon;

import im.dangmoo.benefit.api.usecase.ApiMessage;

public record CouponIssuableResponse(
    boolean issuable,
    String reason,
    boolean isTimeAttack
) {

    public static CouponIssuableResponse ofIssuable(final boolean isTimeAttack) {
        return new CouponIssuableResponse(true, null, isTimeAttack);
    }

    public static CouponIssuableResponse ofNotIssuable(final ApiMessage reason, final boolean isTimeAttack) {
        return new CouponIssuableResponse(false, reason.name(), isTimeAttack);
    }
}
