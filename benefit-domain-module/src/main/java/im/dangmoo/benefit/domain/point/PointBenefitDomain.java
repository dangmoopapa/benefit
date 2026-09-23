package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitWeightOption;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PointBenefitDomain {

    private final PointBenefitType type;
    private final Long amount;
    private final Long minAmount;
    private final Long maxAmount;
    private final List<Long> amounts;
    private final List<PointBenefitWeightOption> options;

    private PointBenefitDomain(
        final PointBenefitType type,
        final Long amount,
        final Long minAmount,
        final Long maxAmount,
        final List<Long> amounts,
        final List<PointBenefitWeightOption> options
    ) {
        this.type = type;
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.amounts = amounts;
        this.options = options;
    }

    public static PointBenefitDomain of(final PointPolicyDocument policy) {
        return of(policy.getBenefitCondition());
    }

    public static PointBenefitDomain of(final PointBenefitCondition benefitCondition) {
        return new PointBenefitDomain(
            benefitCondition.getType(),
            benefitCondition.getAmount(),
            benefitCondition.getMinAmount(),
            benefitCondition.getMaxAmount(),
            benefitCondition.getAmounts(),
            benefitCondition.getOptions()
        );
    }

    public long grantAmount() {
        return grantAmount(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
    }

    public long grantAmount(final long randomSeed) {
        return switch (type) {
            case FIXED -> amount;
            case RANDOM_RANGE -> minAmount + Math.floorMod(randomSeed, maxAmount - minAmount + 1);
            case RANDOM_AMOUNTS -> amounts.get(Math.floorMod(randomSeed, amounts.size()));
            case RANDOM_WEIGHTED -> weightedAmountBy(randomSeed);
        };
    }

    public boolean isGrantAmountValid() {
        return switch (type) {
            case FIXED -> amount != null && amount > 0;
            case RANDOM_RANGE -> minAmount != null
                && maxAmount != null
                && minAmount > 0
                && maxAmount >= minAmount;
            case RANDOM_AMOUNTS -> amounts != null
                && !amounts.isEmpty()
                && amounts.stream().allMatch(candidate -> candidate != null && candidate > 0);
            case RANDOM_WEIGHTED -> options != null
                && !options.isEmpty()
                && options.stream().allMatch(option -> option.getAmount() > 0 && option.getWeight() > 0);
        };
    }

    private long weightedAmountBy(final long randomSeed) {
        long totalWeight = 0L;
        for (final PointBenefitWeightOption option : options) {
            totalWeight += option.getWeight();
        }
        long cursor = Math.floorMod(randomSeed, totalWeight);
        for (final PointBenefitWeightOption option : options) {
            cursor -= option.getWeight();
            if (cursor < 0) {
                return option.getAmount();
            }
        }
        return options.getLast().getAmount();
    }
}
