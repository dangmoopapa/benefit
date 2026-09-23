package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitWeightOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointBenefitDomainTest {

    @Test
    @DisplayName("FIXED 는 설정 금액을 그대로 지급한다")
    void grantAmount_fixed() {
        final PointBenefitDomain pointBenefit = PointBenefitDomain.of(fixedAmount(100L));
        assertThat(pointBenefit.grantAmount(0L)).isEqualTo(100L);
    }

    @Test
    @DisplayName("RANDOM_RANGE 는 min~max 안에서 지급한다")
    void grantAmount_randomRange() {
        final PointBenefitDomain pointBenefit = PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_RANGE, null, 1L, 10L, null, null)
        );
        assertThat(pointBenefit.grantAmount(0L)).isEqualTo(1L);
        assertThat(pointBenefit.grantAmount(9L)).isEqualTo(10L);
        assertThat(pointBenefit.grantAmount(10L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("RANDOM_AMOUNTS 는 목록에서 하나를 지급한다")
    void grantAmount_randomAmounts() {
        final PointBenefitDomain pointBenefit = PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_AMOUNTS, null, null, null, List.of(1L, 2L, 5L), null
            )
        );
        assertThat(pointBenefit.grantAmount(0L)).isEqualTo(1L);
        assertThat(pointBenefit.grantAmount(1L)).isEqualTo(2L);
        assertThat(pointBenefit.grantAmount(2L)).isEqualTo(5L);
        assertThat(pointBenefit.grantAmount(3L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("RANDOM_WEIGHTED 는 가중치 비율로 지급한다")
    void grantAmount_randomWeighted() {
        final PointBenefitDomain pointBenefit = PointBenefitDomain.of(
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
        assertThat(pointBenefit.grantAmount(0L)).isEqualTo(1L);
        assertThat(pointBenefit.grantAmount(1L)).isEqualTo(10L);
        assertThat(pointBenefit.grantAmount(2L)).isEqualTo(10L);
        assertThat(pointBenefit.grantAmount(3L)).isEqualTo(10L);
        assertThat(pointBenefit.grantAmount(4L)).isEqualTo(1L);
    }

    @Test
    @DisplayName("지급 방식이 없는 문서는 FIXED 로 취급한다")
    void grantAmount_nullTypeDefaultsToFixed() {
        final PointBenefitDomain pointBenefit = PointBenefitDomain.of(
            PointBenefitCondition.create(null, 50L, null, null, null, null)
        );
        assertThat(pointBenefit.grantAmount(0L)).isEqualTo(50L);
    }

    @Test
    @DisplayName("지급 금액이 0 이하이거나 비어 있으면 유효하지 않다")
    void isGrantAmountValid_invalid() {
        assertThat(PointBenefitDomain.of(fixedAmount(0L)).isGrantAmountValid()).isFalse();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_RANGE, null, 10L, 1L, null, null)
        ).isGrantAmountValid()).isFalse();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_AMOUNTS, null, null, null, List.of(), null)
        ).isGrantAmountValid()).isFalse();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_WEIGHTED,
                null,
                null,
                null,
                null,
                List.of(PointBenefitWeightOption.create(1L, 0L))
            )
        ).isGrantAmountValid()).isFalse();
    }

    @Test
    @DisplayName("지급 금액이 모두 양수면 유효하다")
    void isGrantAmountValid_valid() {
        assertThat(PointBenefitDomain.of(fixedAmount(100L)).isGrantAmountValid()).isTrue();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_RANGE, null, 1L, 10L, null, null)
        ).isGrantAmountValid()).isTrue();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(PointBenefitType.RANDOM_AMOUNTS, null, null, null, List.of(1L, 2L), null)
        ).isGrantAmountValid()).isTrue();
        assertThat(PointBenefitDomain.of(
            PointBenefitCondition.create(
                PointBenefitType.RANDOM_WEIGHTED,
                null,
                null,
                null,
                null,
                List.of(PointBenefitWeightOption.create(1L, 1L))
            )
        ).isGrantAmountValid()).isTrue();
    }

    private static PointBenefitCondition fixedAmount(final long amount) {
        return PointBenefitCondition.create(PointBenefitType.FIXED, amount, null, null, null, null);
    }
}
