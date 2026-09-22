package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MembershipBenefitDomainTest {

    @Test
    @DisplayName("SEASON_1 + Season1 혜택이면 통과한다")
    void season1_ready() {
        assertThatCode(() -> MembershipBenefitDomain.requireReady(
            MembershipSeason.SEASON_1,
            Season1MembershipBenefit.create(new BigDecimal("0.1"), new BigDecimal("0.05"), "coupon-key")
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("SEASON_2 + Season2 혜택이면 통과한다")
    void season2_ready() {
        assertThatCode(() -> MembershipBenefitDomain.requireReady(
            MembershipSeason.SEASON_2,
            Season2MembershipBenefit.create(
                new BigDecimal("0.1"),
                List.of("cat-1"),
                new BigDecimal("0.05"),
                true,
                false
            )
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("시즌과 혜택 타입이 다르면 PreparingException")
    void mismatchedType() {
        assertThatThrownBy(() -> MembershipBenefitDomain.requireReady(
            MembershipSeason.SEASON_1,
            Season2MembershipBenefit.create(
                new BigDecimal("0.1"),
                List.of("cat-1"),
                new BigDecimal("0.05"),
                true,
                false
            )
        )).isInstanceOf(MembershipBenefitDomain.PreparingException.class);

        assertThatThrownBy(() -> MembershipBenefitDomain.requireReady(
            MembershipSeason.SEASON_2,
            Season1MembershipBenefit.create(new BigDecimal("0.1"), new BigDecimal("0.05"), "coupon-key")
        )).isInstanceOf(MembershipBenefitDomain.PreparingException.class);
    }

    @Test
    @DisplayName("benefit 이 null 이면 PreparingException")
    void nullBenefit() {
        assertThatThrownBy(() -> MembershipBenefitDomain.requireReady(MembershipSeason.SEASON_1, null))
            .isInstanceOf(MembershipBenefitDomain.PreparingException.class);
    }
}
