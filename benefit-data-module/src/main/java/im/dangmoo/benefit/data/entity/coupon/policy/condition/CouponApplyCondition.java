package im.dangmoo.benefit.data.entity.coupon.policy.condition;

import java.util.ArrayList;
import java.util.List;

public class CouponApplyCondition {

    private List<String> productIds = new ArrayList<>();
    private List<String> categoryIds = new ArrayList<>();
    private List<String> brandIds = new ArrayList<>();
    private String segmentId;

    private CouponApplyCondition() {
    }

    public static CouponApplyCondition create(
        final List<String> productIds,
        final List<String> categoryIds,
        final List<String> brandIds,
        final String segmentId
    ) {
        final CouponApplyCondition condition = new CouponApplyCondition();
        condition.productIds = productIds == null ? new ArrayList<>() : productIds;
        condition.categoryIds = categoryIds == null ? new ArrayList<>() : categoryIds;
        condition.brandIds = brandIds == null ? new ArrayList<>() : brandIds;
        condition.segmentId = segmentId;
        return condition;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public List<String> getBrandIds() {
        return brandIds;
    }

    public String getSegmentId() {
        return segmentId;
    }
}
