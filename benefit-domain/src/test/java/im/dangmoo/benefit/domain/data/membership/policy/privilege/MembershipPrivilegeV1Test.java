package im.dangmoo.benefit.domain.data.membership.policy.privilege;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipPrivilegeV1Test {

    @Test
    @DisplayName("결제 금액의 10%를 캐시백한다")
    void cashbackTenPercent() {
        assertThat(new MembershipPrivilegeV1().cashbackPoint(10_000L)).isEqualTo(1_000L);
    }

    @Test
    @DisplayName("10원 미만은 캐시백하지 않는다")
    void cashbackFloorsToZero() {
        final MembershipPrivilegeV1 privilege = new MembershipPrivilegeV1();
        assertThat(privilege.cashbackPoint(9L)).isZero();
        assertThat(privilege.cashbackPoint(0L)).isZero();
        assertThat(privilege.cashbackPoint(-1L)).isZero();
    }
}
