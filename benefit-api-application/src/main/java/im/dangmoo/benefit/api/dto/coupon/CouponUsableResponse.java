package im.dangmoo.benefit.api.dto.coupon;

import java.time.Instant;
import java.util.List;

public record CouponUsableResponse(List<Item> items) {

    public static CouponUsableResponse of(final List<Item> items) {
        return new CouponUsableResponse(items);
    }

    public record Item(
        String walletId,
        String policyId,
        String policyKey,
        String policyName,
        Instant expiresAt
    ) {
    }
}
