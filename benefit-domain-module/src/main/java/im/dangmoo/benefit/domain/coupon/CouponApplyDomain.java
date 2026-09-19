package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponApplyCondition;

import java.util.List;

public class CouponApplyDomain {

    private final List<String> productIds;
    private final List<String> categoryIds;
    private final List<String> brandIds;
    private final String segmentId;

    private CouponApplyDomain(
        final List<String> productIds,
        final List<String> categoryIds,
        final List<String> brandIds,
        final String segmentId
    ) {
        this.productIds = productIds;
        this.categoryIds = categoryIds;
        this.brandIds = brandIds;
        this.segmentId = segmentId;
    }

    public static CouponApplyDomain of(final CouponApplyCondition condition) {
        return new CouponApplyDomain(
            condition.getProductIds(),
            condition.getCategoryIds(),
            condition.getBrandIds(),
            condition.getSegmentId()
        );
    }

    public boolean belongsTo(final String productId, final String brandId) {
        return contains(productIds, productId) || contains(brandIds, brandId);
    }

    public boolean isSatisfied(
        final String productId,
        final String categoryId,
        final String brandId,
        final String requestSegmentId
    ) {
        if (excludes(productIds, productId)) {
            return false;
        }
        if (excludes(categoryIds, categoryId)) {
            return false;
        }
        if (excludes(brandIds, brandId)) {
            return false;
        }
        return !hasText(segmentId) || segmentId.equals(requestSegmentId);
    }

    private boolean excludes(final List<String> values, final String target) {
        return !values.isEmpty() && !contains(values, target);
    }

    private boolean contains(final List<String> values, final String target) {
        return hasText(target) && values.contains(target);
    }

    private boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }
}
