package im.dangmoo.benefit.domain.data.coupon.policy.lifecycle;

public class CouponAccountingCondition {

    private CouponCostBearer bearer;
    private String costCenter;
    private String accountCode;

    private CouponAccountingCondition() {
    }

    public static CouponAccountingCondition create(
        final CouponCostBearer bearer,
        final String costCenter,
        final String accountCode
    ) {
        final CouponAccountingCondition entity = new CouponAccountingCondition();
        entity.bearer = bearer;
        entity.costCenter = costCenter;
        entity.accountCode = accountCode;
        return entity;
    }

    public CouponCostBearer getBearer() {
        return bearer;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getAccountCode() {
        return accountCode;
    }
}
