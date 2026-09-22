package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitWeightOption;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PointBenefitDomain {

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

    public static PointBenefitDomain of(final PointBenefitCondition condition) {
        return new PointBenefitDomain(
            condition.getType(),
            condition.getAmount(),
            condition.getMinAmount(),
            condition.getMaxAmount(),
            condition.getAmounts(),
            condition.getOptions()
        );
    }

    public boolean isValid() {
        return switch (type) {
            case FIXED -> amount != null && amount > 0;
            case RANDOM_RANGE -> minAmount != null
                && maxAmount != null
                && minAmount > 0
                && maxAmount >= minAmount;
            case RANDOM_AMOUNTS -> amounts != null
                && !amounts.isEmpty()
                && amounts.stream().allMatch(value -> value != null && value > 0);
            case RANDOM_WEIGHTED -> options != null
                && !options.isEmpty()
                && options.stream().allMatch(option -> option.getAmount() > 0 && option.getWeight() > 0);
        };
    }

    public long resolveAmount() {
        return resolveAmount(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
    }

    public long resolveAmount(final long randomSeed) {
        return switch (type) {
            case FIXED -> amount;
            case RANDOM_RANGE -> {
                final long span = maxAmount - minAmount + 1;
                yield minAmount + Math.floorMod(randomSeed, span);
            }
            case RANDOM_AMOUNTS -> amounts.get(Math.floorMod(randomSeed, amounts.size()));
            case RANDOM_WEIGHTED -> {
                long total = 0L;
                for (final PointBenefitWeightOption option : options) {
                    total += option.getWeight();
                }
                long cursor = Math.floorMod(randomSeed, total);
                for (final PointBenefitWeightOption option : options) {
                    cursor -= option.getWeight();
                    if (cursor < 0) {
                        yield option.getAmount();
                    }
                }
                yield options.getLast().getAmount();
            }
        };
    }
}
