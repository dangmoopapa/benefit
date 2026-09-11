package im.dangmoo.benefit.domain.data.coupon.policy.apply;

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
        final CouponApplyCondition entity = new CouponApplyCondition();
        entity.unit = unit;
        entity.include = include;
        entity.exclude = exclude;
        return entity;
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
