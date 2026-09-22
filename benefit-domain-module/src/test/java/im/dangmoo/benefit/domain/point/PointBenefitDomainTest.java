package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointBenefitWeightOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointBenefitDomainTest {

    @Test
    @DisplayName("FIXED 는 amount 를 그대로 반환한다")
    void fixed_resolveAmount() {
        final PointBenefitDomain domain = PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.FIXED, 100L, null, null, null, null)
        );
        assertThat(domain.isValid()).isTrue();
        assertThat(domain.resolveAmount(0L)).isEqualTo(100L);
    }

    @Test
    @DisplayName("FIXED amount 가 0 이하면 유효하지 않다")
    void fixed_invalid() {
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.FIXED, 0L, null, null, null, null)
        ).isValid()).isFalse();
    }

    @Test
    @DisplayName("RANDOM_RANGE 는 min~max inclusive 에서 고른다")
    void randomRange_resolveAmount() {
        final PointBenefitDomain domain = PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_RANGE, null, 1L, 10L, null, null)
        );
        assertThat(domain.isValid()).isTrue();
        assertThat(domain.resolveAmount(0L)).isEqualTo(1L);
        assertThat(domain.resolveAmount(9L)).isEqualTo(10L);
        assertThat(domain.resolveAmount(10L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("RANDOM_RANGE min>max 이면 유효하지 않다")
    void randomRange_invalid() {
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_RANGE, null, 10L, 1L, null, null)
        ).isValid()).isFalse();
    }

    @Test
    @DisplayName("RANDOM_AMOUNTS 는 목록에서 고른다")
    void randomAmounts_resolveAmount() {
        final PointBenefitDomain domain = PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_AMOUNTS,
                null,
                null,
                null,
                List.of(1L, 2L, 5L),
                null
            )
        );
        assertThat(domain.isValid()).isTrue();
        assertThat(domain.resolveAmount(0L)).isEqualTo(1L);
        assertThat(domain.resolveAmount(1L)).isEqualTo(2L);
        assertThat(domain.resolveAmount(2L)).isEqualTo(5L);
        assertThat(domain.resolveAmount(3L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("RANDOM_AMOUNTS 가 비어 있으면 유효하지 않다")
    void randomAmounts_invalid() {
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_AMOUNTS, null, null, null, List.of(), null)
        ).isValid()).isFalse();
    }

    @Test
    @DisplayName("RANDOM_WEIGHTED 는 weight 비율로 고른다")
    void randomWeighted_resolveAmount() {
        final PointBenefitDomain domain = PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_WEIGHTED,
                null,
                null,
                null,
                null,
                List.of(
                    PointBenefitWeightOption.create(1L, 1L),
                    PointBenefitWeightOption.create(10L, 3L)
                )
            )
        );
        assertThat(domain.isValid()).isTrue();
        assertThat(domain.resolveAmount(0L)).isEqualTo(1L);
        assertThat(domain.resolveAmount(1L)).isEqualTo(10L);
        assertThat(domain.resolveAmount(2L)).isEqualTo(10L);
        assertThat(domain.resolveAmount(3L)).isEqualTo(10L);
        assertThat(domain.resolveAmount(4L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("RANDOM_WEIGHTED weight 가 0 이면 유효하지 않다")
    void randomWeighted_invalid() {
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_WEIGHTED,
                null,
                null,
                null,
                null,
                List.of(PointBenefitWeightOption.create(1L, 0L))
            )
        ).isValid()).isFalse();
    }

    @Test
    @DisplayName("type 이 null 인 문서는 FIXED 로 취급한다")
    void nullType_defaultsToFixed() {
        final PointBenefitDomain domain = PointBenefitDomain.of(
            PointBenefitCondition.create(null, 50L, null, null, null, null)
        );
        assertThat(domain.isValid()).isTrue();
        assertThat(domain.resolveAmount(0L)).isEqualTo(50L);
    }
}
