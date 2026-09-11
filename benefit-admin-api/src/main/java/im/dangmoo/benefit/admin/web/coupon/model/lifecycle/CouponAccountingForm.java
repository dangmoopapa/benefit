package im.dangmoo.benefit.admin.web.coupon.model.lifecycle;

import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponAccountingCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponCostBearer;

public record CouponAccountingForm(
    CouponCostBearer bearer,
    String costCenter,
    String accountCode
) {

    public CouponAccountingCondition toEntity() {
        return CouponAccountingCondition.create(bearer, costCenter, accountCode);
    }

    public static CouponAccountingForm of(final CouponAccountingCondition entity) {
        return new CouponAccountingForm(
            entity.getBearer(),
            entity.getCostCenter(),
            entity.getAccountCode()
        );
    }
}
