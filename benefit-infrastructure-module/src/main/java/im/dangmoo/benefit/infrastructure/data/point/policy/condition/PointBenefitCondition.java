package im.dangmoo.benefit.infrastructure.data.point.policy.condition;

public class PointBenefitCondition {

    private long amount;

    private PointBenefitCondition() {
    }

    public static PointBenefitCondition create(final long amount) {
        final PointBenefitCondition condition = new PointBenefitCondition();
        condition.amount = amount;
        return condition;
    }

    public long getAmount() {
        return amount;
    }
}
