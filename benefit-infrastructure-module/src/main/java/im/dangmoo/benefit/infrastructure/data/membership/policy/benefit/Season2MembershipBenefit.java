package im.dangmoo.benefit.infrastructure.data.membership.policy.benefit;

import org.springframework.data.annotation.TypeAlias;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TypeAlias("SEASON_2")
public class Season2MembershipBenefit extends MembershipBenefit {

    private BigDecimal categoryDiscountRate;
    private List<String> categoryIds = new ArrayList<>();
    private BigDecimal pointCashbackRate;
    private boolean freeShipping;
    private boolean freeDelivery;

    private Season2MembershipBenefit() {
    }

    public static Season2MembershipBenefit create(
        final BigDecimal categoryDiscountRate,
        final List<String> categoryIds,
        final BigDecimal pointCashbackRate,
        final boolean freeShipping,
        final boolean freeDelivery
    ) {
        final Season2MembershipBenefit benefit = new Season2MembershipBenefit();
        benefit.categoryDiscountRate = categoryDiscountRate;
        benefit.categoryIds = categoryIds == null ? new ArrayList<>() : new ArrayList<>(categoryIds);
        benefit.pointCashbackRate = pointCashbackRate;
        benefit.freeShipping = freeShipping;
        benefit.freeDelivery = freeDelivery;
        return benefit;
    }

    public BigDecimal getCategoryDiscountRate() {
        return categoryDiscountRate;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public BigDecimal getPointCashbackRate() {
        return pointCashbackRate;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public boolean isFreeDelivery() {
        return freeDelivery;
    }
}
