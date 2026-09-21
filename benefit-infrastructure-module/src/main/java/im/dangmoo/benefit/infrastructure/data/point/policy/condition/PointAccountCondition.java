package im.dangmoo.benefit.infrastructure.data.point.policy.condition;

public class PointAccountCondition {

    private String accountKey;

    private PointAccountCondition() {
    }

    public static PointAccountCondition create(final String accountKey) {
        final PointAccountCondition condition = new PointAccountCondition();
        condition.accountKey = accountKey;
        return condition;
    }

    public String getAccountKey() {
        return accountKey;
    }
}
