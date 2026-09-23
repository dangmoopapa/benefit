package im.dangmoo.benefit.data.entity.membership.history;

import java.math.BigDecimal;

public class MembershipBenefitApplied {

    private BigDecimal discountAmount;
    private BigDecimal cashbackAmount;
    private boolean freeShipping;
    private boolean freeDelivery;

    private MembershipBenefitApplied() {
    }

    public static MembershipBenefitApplied of(
        final BigDecimal discountAmount,
        final BigDecimal cashbackAmount,
        final boolean freeShipping,
        final boolean freeDelivery
    ) {
        final MembershipBenefitApplied applied = new MembershipBenefitApplied();
        applied.discountAmount = discountAmount;
        applied.cashbackAmount = cashbackAmount;
        applied.freeShipping = freeShipping;
        applied.freeDelivery = freeDelivery;
        return applied;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getCashbackAmount() {
        return cashbackAmount;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public boolean isFreeDelivery() {
        return freeDelivery;
    }
}
