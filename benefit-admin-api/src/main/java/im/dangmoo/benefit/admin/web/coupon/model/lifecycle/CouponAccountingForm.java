package im.dangmoo.benefit.admin.web.coupon.model.lifecycle;

import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponAccountingCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponCostBearer;

public record CouponAccountingForm(
    CouponCostBearer bearer,
    String costCenter,
    String accountCode
) {

    public CouponAccountingCondition toDocument() {
        return CouponAccountingCondition.create(bearer, costCenter, accountCode);
    }

    public static CouponAccountingForm of(final CouponAccountingCondition document) {
        return new CouponAccountingForm(
            document.getBearer(),
            document.getCostCenter(),
            document.getAccountCode()
        );
    }
}
