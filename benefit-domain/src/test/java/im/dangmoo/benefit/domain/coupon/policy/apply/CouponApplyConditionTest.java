package im.dangmoo.benefit.domain.coupon.policy.apply;

import im.dangmoo.benefit.domain.coupon.document.policy.apply.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponApplyConditionTest {

    @Test
    @DisplayName("전체 상품이면 포함 조건을 통과한다")
    void passesWhenAllProducts() {
        final CouponApplyCondition condition = CouponApplyCondition.create(
            CouponApplyUnit.ORDER,
            CouponApplyInclude.create(true, List.of(), List.of(), List.of(), List.of(), List.of()),
            CouponApplyExclude.create(List.of(), List.of(), false, false)
        );

        assertThat(condition.isSatisfiedBy(
            new CouponApplyTarget(List.of("p1"), List.of("c1"), List.of(), List.of(), List.of(), false, false)
        )).isTrue();
    }

    @Test
    @DisplayName("포함 상품에 없으면 실패한다")
    void failsWhenProductNotIncluded() {
        final CouponApplyCondition condition = CouponApplyCondition.create(
            CouponApplyUnit.PRODUCT,
            CouponApplyInclude.create(false, List.of("p1"), List.of(), List.of(), List.of(), List.of()),
            CouponApplyExclude.create(List.of(), List.of(), false, false)
        );

        assertThat(condition.isSatisfiedBy(
            new CouponApplyTarget(List.of("p2"), List.of(), List.of(), List.of(), List.of(), false, false)
        )).isFalse();
    }

    @Test
    @DisplayName("제외 카테고리에 있으면 실패한다")
    void failsWhenCategoryExcluded() {
        final CouponApplyCondition condition = CouponApplyCondition.create(
            CouponApplyUnit.ORDER,
            CouponApplyInclude.create(true, List.of(), List.of(), List.of(), List.of(), List.of()),
            CouponApplyExclude.create(List.of(), List.of("c1"), false, false)
        );

        assertThat(condition.isSatisfiedBy(
            new CouponApplyTarget(List.of("p1"), List.of("c1"), List.of(), List.of(), List.of(), false, false)
        )).isFalse();
    }

    @Test
    @DisplayName("이미 할인된 상품을 제외하는데 해당하면 실패한다")
    void failsWhenAlreadyDiscountedExcluded() {
        final CouponApplyCondition condition = CouponApplyCondition.create(
            CouponApplyUnit.ORDER,
            CouponApplyInclude.create(true, List.of(), List.of(), List.of(), List.of(), List.of()),
            CouponApplyExclude.create(List.of(), List.of(), true, false)
        );

        assertThat(condition.isSatisfiedBy(
            new CouponApplyTarget(List.of("p1"), List.of(), List.of(), List.of(), List.of(), true, false)
        )).isFalse();
    }

    @Test
    @DisplayName("포함·제외가 모두 맞으면 통과한다")
    void passesWhenIncludeAndExcludeMatch() {
        final CouponApplyCondition condition = CouponApplyCondition.create(
            CouponApplyUnit.PRODUCT,
            CouponApplyInclude.create(false, List.of("p1"), List.of("c1"), List.of(), List.of(), List.of()),
            CouponApplyExclude.create(List.of("p9"), List.of(), false, true)
        );

        assertThat(condition.isSatisfiedBy(
            new CouponApplyTarget(List.of("p1"), List.of("c1"), List.of(), List.of(), List.of(), false, false)
        )).isTrue();
    }
}
