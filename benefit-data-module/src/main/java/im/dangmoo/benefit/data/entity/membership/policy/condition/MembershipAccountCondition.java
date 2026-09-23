package im.dangmoo.benefit.data.entity.membership.policy.condition;

public class MembershipAccountCondition {

    private String accountKey;

    private MembershipAccountCondition() {
    }

    public static MembershipAccountCondition create(final String accountKey) {
        final MembershipAccountCondition condition = new MembershipAccountCondition();
        condition.accountKey = accountKey;
        return condition;
    }

    public String getAccountKey() {
        return accountKey;
    }
}
