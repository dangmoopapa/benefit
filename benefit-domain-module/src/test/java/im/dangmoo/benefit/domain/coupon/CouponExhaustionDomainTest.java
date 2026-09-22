package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponExhaustionDomainTest {

    @Test
    @DisplayName("재고 제한이 없으면 소진되지 않는다")
    void isExhausted_unlimited() {
        final CouponExhaustionDomain domain = CouponExhaustionDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.isExhausted(0L)).isFalse();
        assertThat(domain.isExhausted(100L)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
        "10, 0, false",
        "10, 9, false",
        "10, 10, true",
        "10, 11, true",
        "0, 0, true"
    })
    @DisplayName("issuedCount 가 stockQuantity 이상이면 소진이다")
    void isExhausted_boundaries(final long stock, final long issued, final boolean expected) {
        final CouponExhaustionDomain domain = CouponExhaustionDomain.of(
            CouponIssueCondition.create(null, null, stock, List.of(), List.of())
        );
        assertThat(domain.isExhausted(issued)).isEqualTo(expected);
    }

    @Test
    @DisplayName("재고 제한이 없으면 방금 소진도 아니다")
    void isJustExhausted_unlimited() {
        final CouponExhaustionDomain domain = CouponExhaustionDomain.of(
            CouponIssueCondition.create(null, null, null, List.of(), List.of())
        );
        assertThat(domain.isJustExhausted(1L)).isFalse();
    }

    @Test
    @DisplayName("발급 직후 수량이 재고와 같으면 방금 소진이다")
    void isJustExhausted_exactMatch() {
        final CouponExhaustionDomain domain = CouponExhaustionDomain.of(
            CouponIssueCondition.create(null, null, 10L, List.of(), List.of())
        );
        assertThat(domain.isJustExhausted(10L)).isTrue();
        assertThat(domain.isJustExhausted(9L)).isFalse();
        assertThat(domain.isJustExhausted(11L)).isFalse();
    }
}
