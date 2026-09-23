package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyCache;
import im.dangmoo.benefit.data.entity.coupon.policy.CouponPolicyDocument;
import im.dangmoo.benefit.data.entity.coupon.policy.condition.CouponApplyCondition;

import java.util.List;

public final class CouponApplyDomain {

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

    public static CouponApplyDomain of(final CouponPolicyDocument policy) {
        return of(policy.getApplyCondition());
    }

    public static CouponApplyDomain of(final CouponPolicyCache policy) {
        return of(policy.applyCondition());
    }

    static CouponApplyDomain of(final CouponApplyCondition applyCondition) {
        return new CouponApplyDomain(
            applyCondition.getProductIds(),
            applyCondition.getCategoryIds(),
            applyCondition.getBrandIds(),
            applyCondition.getSegmentId()
        );
    }

    public boolean covers(final String productId, final String brandId) {
        return contains(productIds, productId) || contains(brandIds, brandId);
    }

    public boolean isApplicableTo(
        final String productId,
        final String categoryId,
        final String brandId,
        final String segmentId
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
        return !hasText(this.segmentId) || this.segmentId.equals(segmentId);
    }

    private boolean excludes(final List<String> allowedIds, final String target) {
        return !allowedIds.isEmpty() && !contains(allowedIds, target);
    }

    private boolean contains(final List<String> allowedIds, final String target) {
        return hasText(target) && allowedIds.contains(target);
    }

    private boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }
}
