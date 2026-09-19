package im.dangmoo.benefit.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CouponWalletDomainTest {

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, policy-1:user-1",
        "abc, xyz, abc:xyz",
        "p, u, p:u"
    })
    @DisplayName("idempotencyKey 는 policyId:userId 형식이다")
    void idempotencyKey_format(final String policyId, final String userId, final String expected) {
        assertThat(CouponWalletDomain.idempotencyKey(policyId, userId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("같은 입력이면 항상 같은 키를 만든다")
    void idempotencyKey_stable() {
        final String first = CouponWalletDomain.idempotencyKey("p", "u");
        final String second = CouponWalletDomain.idempotencyKey("p", "u");
        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("policyId 또는 userId 가 다르면 키가 다르다")
    void idempotencyKey_distinct() {
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1"))
            .isNotEqualTo(CouponWalletDomain.idempotencyKey("p2", "u1"));
        assertThat(CouponWalletDomain.idempotencyKey("p1", "u1"))
            .isNotEqualTo(CouponWalletDomain.idempotencyKey("p1", "u2"));
    }
}
