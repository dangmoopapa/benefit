package im.dangmoo.benefit.infrastructure.data.membership.policy.benefit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import im.dangmoo.benefit.infrastructure.data.membership.history.MembershipBenefitApplied;

import java.math.BigDecimal;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Season1MembershipBenefit.class, name = "SEASON_1"),
    @JsonSubTypes.Type(value = Season2MembershipBenefit.class, name = "SEASON_2")
})
public abstract class MembershipBenefit {

    public abstract MembershipBenefitApplied apply(final BigDecimal paymentAmount, final String categoryId);

    public abstract Optional<String> monthlyCouponPolicyKey();

    protected static BigDecimal rateOf(final BigDecimal amount, final BigDecimal rate) {
        if (amount == null || rate == null || rate.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).setScale(0, java.math.RoundingMode.DOWN);
    }
}
