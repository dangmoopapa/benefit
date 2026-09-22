package im.dangmoo.benefit.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PointTransactionDomainTest {

    @ParameterizedTest
    @CsvSource({
        "policy-1, user-1, GRANT:policy-1:user-1",
        "p, u, GRANT:p:u"
    })
    @DisplayName("grantKey 는 GRANT:policyId:userId 형식이다")
    void grantKey_format(final String policyId, final String userId, final String expected) {
        assertThat(PointTransactionDomain.grantKey(policyId, userId)).isEqualTo(expected);
    }

    @Test
    @DisplayName("useKey 는 USE:orderId 형식이다")
    void useKey_format() {
        assertThat(PointTransactionDomain.useKey("order-1")).isEqualTo("USE:order-1");
    }

    @Test
    @DisplayName("입력이 다르면 키가 다르다")
    void keys_distinct() {
        assertThat(PointTransactionDomain.grantKey("p1", "u1"))
            .isNotEqualTo(PointTransactionDomain.grantKey("p2", "u1"));
        assertThat(PointTransactionDomain.useKey("o1"))
            .isNotEqualTo(PointTransactionDomain.useKey("o2"));
    }
}
