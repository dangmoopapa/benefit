package im.dangmoo.benefit.data.entity.point.policy.condition;

import java.util.ArrayList;
import java.util.List;

public class PointBenefitCondition {

    private PointBenefitType type;
    private Long amount;
    private Long minAmount;
    private Long maxAmount;
    private List<Long> amounts = new ArrayList<>();
    private List<PointBenefitWeightOption> options = new ArrayList<>();

    private PointBenefitCondition() {
    }

    public static PointBenefitCondition create(
        final PointBenefitType type,
        final Long amount,
        final Long minAmount,
        final Long maxAmount,
        final List<Long> amounts,
        final List<PointBenefitWeightOption> options
    ) {
        final PointBenefitCondition condition = new PointBenefitCondition();
        condition.type = type;
        condition.amount = amount;
        condition.minAmount = minAmount;
        condition.maxAmount = maxAmount;
        condition.amounts = amounts == null ? new ArrayList<>() : new ArrayList<>(amounts);
        condition.options = options == null ? new ArrayList<>() : new ArrayList<>(options);
        return condition;
    }

    public PointBenefitType getType() {
        return type == null ? PointBenefitType.FIXED : type;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getMinAmount() {
        return minAmount;
    }

    public Long getMaxAmount() {
        return maxAmount;
    }

    public List<Long> getAmounts() {
        return amounts;
    }

    public List<PointBenefitWeightOption> getOptions() {
        return options;
    }
}
