package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponExhaustionDomainTest {

    private static CouponExhaustionDomain domain(final Long stockQuantity) {
        return CouponExhaustionDomain.of(
            CouponIssueCondition.create(null, null, stockQuantity, List.of(), List.of())
        );
    }

    @Nested
    @DisplayName("isExhausted")
    class IsExhausted {

        @Test
        @DisplayName("재고 제한이 없으면 소진되지 않는다")
        void unlimited_neverExhausted() {
            final CouponExhaustionDomain domain = domain(null);
            assertThat(domain.isExhausted(0)).isFalse();
            assertThat(domain.isExhausted(1_000_000)).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
            "100, 0, false",
            "100, 99, false",
            "100, 100, true",
            "100, 101, true",
            "1, 0, false",
            "1, 1, true",
            "0, 0, true"
        })
        @DisplayName("issuedCount >= stockQuantity 이면 소진이다")
        void boundaries(final long stock, final long issued, final boolean exhausted) {
            assertThat(domain(stock).isExhausted(issued)).isEqualTo(exhausted);
        }
    }

    @Nested
    @DisplayName("isJustExhausted")
    class IsJustExhausted {

        @Test
        @DisplayName("재고 제한이 없으면 방금 소진이 아니다")
        void unlimited_neverJustExhausted() {
            assertThat(domain(null).isJustExhausted(0)).isFalse();
            assertThat(domain(null).isJustExhausted(100)).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
            "100, 99, false",
            "100, 100, true",
            "100, 101, false",
            "1, 1, true",
            "1, 0, false",
            "1, 2, false"
        })
        @DisplayName("발급 직후 수량이 정확히 stockQuantity 일 때만 방금 소진이다")
        void onlyExactMatch(final long stock, final long afterIssue, final boolean justExhausted) {
            assertThat(domain(stock).isJustExhausted(afterIssue)).isEqualTo(justExhausted);
        }

        @Test
        @DisplayName("이미 초과한 뒤에는 방금 소진이 아니다")
        void alreadyOver_false() {
            assertThat(domain(10L).isJustExhausted(11)).isFalse();
        }
    }
}
