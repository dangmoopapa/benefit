package im.dangmoo.benefit.admin.web.coupon.model.apply;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyUnit;

public record CouponApplyForm(
    CouponApplyUnit unit,
    CouponApplyIncludeForm include,
    CouponApplyExcludeForm exclude
) {

    public CouponApplyCondition toDocument() {
        return CouponApplyCondition.create(unit, include.toDocument(), exclude.toDocument());
    }

    public static CouponApplyForm of(final CouponApplyCondition document) {
        return new CouponApplyForm(
            document.getUnit(),
            CouponApplyIncludeForm.of(document.getInclude()),
            CouponApplyExcludeForm.of(document.getExclude())
        );
    }
}
