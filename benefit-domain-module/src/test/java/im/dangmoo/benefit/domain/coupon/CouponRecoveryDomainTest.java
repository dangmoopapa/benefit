package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponLifecycleCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CouponRecoveryDomainTest {

    @ParameterizedTest
    @CsvSource({
        "false, false, false",
        "true, false, true",
        "false, true, true",
        "true, true, true"
    })
    @DisplayName("reclaimable 또는 reclaimableOnPaymentCancel 이면 회수 가능하다")
    void isRecoverable(
        final boolean reclaimableOnPaymentCancel,
        final boolean reclaimable,
        final boolean expected
    ) {
        final CouponRecoveryDomain domain = CouponRecoveryDomain.of(
            CouponLifecycleCondition.create(reclaimableOnPaymentCancel, reclaimable)
        );
        assertThat(domain.isRecoverable()).isEqualTo(expected);
    }

    @Test
    @DisplayName("둘 다 false 면 회수 불가하다")
    void bothFalse_notRecoverable() {
        final CouponRecoveryDomain domain = CouponRecoveryDomain.of(
            CouponLifecycleCondition.create(false, false)
        );
        assertThat(domain.isRecoverable()).isFalse();
    }
}
