package im.dangmoo.benefit.data.entity.coupon.policy.condition;

public class CouponAccountCondition {

    private String accountKey;

    private CouponAccountCondition() {
    }

    public static CouponAccountCondition create(final String accountKey) {
        final CouponAccountCondition condition = new CouponAccountCondition();
        condition.accountKey = accountKey;
        return condition;
    }

    public String getAccountKey() {
        return accountKey;
    }
}
