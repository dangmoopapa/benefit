package im.dangmoo.benefit.domain.coupon.policy.lifecycle;

public class CouponAccountingCondition {

    private CouponCostBearer bearer;
    private String costCenter;
    private String accountCode;

    protected CouponAccountingCondition() {
    }

    public static CouponAccountingCondition create(
        final CouponCostBearer bearer,
        final String costCenter,
        final String accountCode
    ) {
        final CouponAccountingCondition document = new CouponAccountingCondition();
        document.bearer = bearer;
        document.costCenter = costCenter;
        document.accountCode = accountCode;
        return document;
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
