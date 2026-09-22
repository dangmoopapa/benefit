package im.dangmoo.benefit.infrastructure.data.point.policy.condition;

public class PointBenefitWeightOption {

    private long amount;
    private long weight;

    private PointBenefitWeightOption() {
    }

    public static PointBenefitWeightOption create(final long amount, final long weight) {
        final PointBenefitWeightOption option = new PointBenefitWeightOption();
        option.amount = amount;
        option.weight = weight;
        return option;
    }

    public long getAmount() {
        return amount;
    }

    public long getWeight() {
        return weight;
    }
}
