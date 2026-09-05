package im.dangmoo.benefit.domain.coupon.document.policy.apply;

import java.util.List;

public record CouponApplyTarget(
    List<String> productIds,
    List<String> categoryIds,
    List<String> brandIds,
    List<String> sellerIds,
    List<String> optionIds,
    boolean alreadyDiscounted,
    boolean otherCouponApplied
) {
}
