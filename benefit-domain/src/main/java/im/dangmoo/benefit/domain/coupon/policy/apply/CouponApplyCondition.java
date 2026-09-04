package im.dangmoo.benefit.domain.coupon.policy.apply;

import java.util.Collection;
import java.util.List;

public class CouponApplyCondition {

    private CouponApplyUnit unit;
    private CouponApplyInclude include;
    private CouponApplyExclude exclude;

    private CouponApplyCondition() {
    }

    public static CouponApplyCondition create(
        final CouponApplyUnit unit,
        final CouponApplyInclude include,
        final CouponApplyExclude exclude
    ) {
        final CouponApplyCondition document = new CouponApplyCondition();
        document.unit = unit;
        document.include = include;
        document.exclude = exclude;
        return document;
    }

    public CouponApplyUnit getUnit() {
        return unit;
    }

    public CouponApplyInclude getInclude() {
        return include;
    }

    public CouponApplyExclude getExclude() {
        return exclude;
    }

    public boolean isSatisfiedBy(final CouponApplyTarget target) {
        if (!matchesInclude(target)) {
            return false;
        }
        return !isExcluded(target);
    }

    private boolean matchesInclude(final CouponApplyTarget target) {
        if (include.isAllProducts()) {
            return true;
        }
        return matchesAxis(include.getProductIds(), target.productIds())
            && matchesAxis(include.getCategoryIds(), target.categoryIds())
            && matchesAxis(include.getBrandIds(), target.brandIds())
            && matchesAxis(include.getSellerIds(), target.sellerIds())
            && matchesAxis(include.getOptionIds(), target.optionIds());
    }

    private boolean isExcluded(final CouponApplyTarget target) {
        if (intersects(exclude.getProductIds(), target.productIds())) {
            return true;
        }
        if (intersects(exclude.getCategoryIds(), target.categoryIds())) {
            return true;
        }
        if (exclude.isAlreadyDiscounted() && target.alreadyDiscounted()) {
            return true;
        }
        return exclude.isOtherCouponApplied() && target.otherCouponApplied();
    }

    private static boolean matchesAxis(final List<String> required, final List<String> actual) {
        if (required == null || required.isEmpty()) {
            return true;
        }
        return intersects(required, actual);
    }

    private static boolean intersects(final Collection<String> left, final Collection<String> right) {
        if (left == null || left.isEmpty() || right == null || right.isEmpty()) {
            return false;
        }
        return left.stream().anyMatch(right::contains);
    }
}
