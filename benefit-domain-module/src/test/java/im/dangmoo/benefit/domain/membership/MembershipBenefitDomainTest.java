package im.dangmoo.benefit.domain.membership;

import im.dangmoo.benefit.infrastructure.data.membership.policy.MembershipSeason;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season1MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.Season2MembershipBenefit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipBenefitDomainTest {

    @Test
    @DisplayName("SEASON_1 + Season1 혜택이면 ready")
    void season1_ready() {
        assertThat(MembershipBenefitDomain.of(
            MembershipSeason.SEASON_1,
            Season1MembershipBenefit.create(new BigDecimal("0.1"), new BigDecimal("0.05"), "coupon-key")
        ).isServiceable()).isTrue();
    }

    @Test
    @DisplayName("SEASON_2 + Season2 혜택이면 ready")
    void season2_ready() {
        assertThat(MembershipBenefitDomain.of(
            MembershipSeason.SEASON_2,
            Season2MembershipBenefit.create(
                new BigDecimal("0.1"),
                List.of("cat-1"),
                new BigDecimal("0.05"),
                true,
                false
            )
        ).isServiceable()).isTrue();
    }

    @Test
    @DisplayName("시즌과 혜택 타입이 다르면 not ready")
    void mismatchedType() {
        assertThat(MembershipBenefitDomain.of(
            MembershipSeason.SEASON_1,
            Season2MembershipBenefit.create(
                new BigDecimal("0.1"),
                List.of("cat-1"),
                new BigDecimal("0.05"),
                true,
                false
            )
        ).isServiceable()).isFalse();

        assertThat(MembershipBenefitDomain.of(
            MembershipSeason.SEASON_2,
            Season1MembershipBenefit.create(new BigDecimal("0.1"), new BigDecimal("0.05"), "coupon-key")
        ).isServiceable()).isFalse();
    }

    @Test
    @DisplayName("benefit 이 null 이면 not ready")
    void nullBenefit() {
        assertThat(MembershipBenefitDomain.of(MembershipSeason.SEASON_1, null).isServiceable()).isFalse();
    }
}
