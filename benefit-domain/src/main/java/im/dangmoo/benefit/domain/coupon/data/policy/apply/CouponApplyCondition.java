package im.dangmoo.benefit.domain.coupon.data.policy.apply;

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
}
