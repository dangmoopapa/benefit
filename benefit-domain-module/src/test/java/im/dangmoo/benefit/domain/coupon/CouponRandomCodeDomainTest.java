package im.dangmoo.benefit.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponRandomCodeDomainTest {

    @Nested
    @DisplayName("generate")
    class Generate {

        @Test
        @DisplayName("길이는 고정이다")
        void fixedLength() {
            assertThat(CouponRandomCodeDomain.generate()).hasSize(CouponRandomCodeDomain.length());
        }

        @Test
        @DisplayName("허용 알파벳만 사용한다")
        void alphabetOnly() {
            for (int i = 0; i < 100; i++) {
                assertThat(CouponRandomCodeDomain.isValidFormat(CouponRandomCodeDomain.generate())).isTrue();
            }
        }

        @Test
        @DisplayName("혼동 문자 I O 0 1 을 포함하지 않는다")
        void excludesConfusingChars() {
            for (int i = 0; i < 200; i++) {
                final String code = CouponRandomCodeDomain.generate();
                assertThat(code).doesNotContain("I", "O", "0", "1");
            }
        }
    }

    @Nested
    @DisplayName("generate(quantity)")
    class GenerateQuantity {

        @Test
        @DisplayName("요청 수량만큼 생성한다")
        void exactQuantity() {
            assertThat(CouponRandomCodeDomain.generate(50)).hasSize(50);
        }

        @Test
        @DisplayName("배치 내 중복이 없다")
        void uniqueInBatch() {
            final List<String> codes = CouponRandomCodeDomain.generate(200);
            assertThat(new HashSet<>(codes)).hasSize(200);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("0 이하이면 빈 리스트다")
        void nonPositive_empty(final int quantity) {
            assertThat(CouponRandomCodeDomain.generate(quantity)).isEmpty();
        }
    }

    @Nested
    @DisplayName("isValidFormat")
    class IsValidFormat {

        @Test
        @DisplayName("정상 포맷은 true")
        void valid_true() {
            assertThat(CouponRandomCodeDomain.isValidFormat(CouponRandomCodeDomain.generate())).isTrue();
        }

        @Test
        @DisplayName("길이 불일치면 false")
        void wrongLength_false() {
            assertThat(CouponRandomCodeDomain.isValidFormat("SHORT")).isFalse();
            assertThat(CouponRandomCodeDomain.isValidFormat("TOOLONGCODE12")).isFalse();
        }

        @Test
        @DisplayName("허용되지 않은 문자가 있으면 false")
        void invalidChar_false() {
            assertThat(CouponRandomCodeDomain.isValidFormat("ABCDEFGHIJ")).isFalse();
            assertThat(CouponRandomCodeDomain.isValidFormat("ABCDEFGH0J")).isFalse();
        }

        @Test
        @DisplayName("null 이면 false")
        void null_false() {
            assertThat(CouponRandomCodeDomain.isValidFormat(null)).isFalse();
        }
    }
}
