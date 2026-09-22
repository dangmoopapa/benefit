package im.dangmoo.benefit.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponRandomCodeDomainTest {

    @Test
    @DisplayName("길이는 10이다")
    void fixedLength() {
        assertThat(CouponRandomCodeDomain.generate()).hasSize(10);
    }

    @Test
    @DisplayName("허용 알파벳만 사용하고 혼동 문자를 포함하지 않는다")
    void alphabetOnly() {
        for (int i = 0; i < 100; i++) {
            final String code = CouponRandomCodeDomain.generate();
            assertThat(code).matches("[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]{10}");
            assertThat(code).doesNotContain("I", "O", "0", "1");
        }
    }
}
