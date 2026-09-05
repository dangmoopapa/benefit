package im.dangmoo.benefit.api.web.coupon.model;

import java.util.List;

public record CouponBoxResponse(
    List<CouponWalletResponse> available,
    List<CouponWalletResponse> unavailable
) {
}
