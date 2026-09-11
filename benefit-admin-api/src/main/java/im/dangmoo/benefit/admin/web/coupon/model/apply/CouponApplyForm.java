package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyUnit;

public record CouponApplyForm(
    CouponApplyUnit unit,
    CouponApplyIncludeForm include,
    CouponApplyExcludeForm exclude
) {

    public CouponApplyCondition toEntity() {
        return CouponApplyCondition.create(unit, include.toEntity(), exclude.toEntity());
    }

    public static CouponApplyForm of(final CouponApplyCondition entity) {
        return new CouponApplyForm(
            entity.getUnit(),
            CouponApplyIncludeForm.of(entity.getInclude()),
            CouponApplyExcludeForm.of(entity.getExclude())
        );
    }
}
