package im.dangmoo.benefit.domain.coupon.policy.lifecycle;

import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponLifecycleCondition;
import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponOnOrderCancel;
import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponOnPartialCancel;
import im.dangmoo.benefit.domain.coupon.document.policy.lifecycle.CouponOnRefund;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponLifecycleConditionTest {

    @Test
    @DisplayName("주문 취소 시 RECOVER면 복구한다")
    void recoversOnOrderCancelWhenRecover() {
        final CouponLifecycleCondition condition = CouponLifecycleCondition.create(
            CouponOnOrderCancel.RECOVER,
            CouponOnPartialCancel.KEEP,
            CouponOnRefund.KEEP_DISCOUNT,
            false,
            null
        );

        assertThat(condition.recoversOnOrderCancel()).isTrue();
    }

    @Test
    @DisplayName("부분 취소 RECOVER_IF_UNUSED는 unused일 때만 복구한다")
    void recoversOnPartialCancelOnlyWhenUnused() {
        final CouponLifecycleCondition condition = CouponLifecycleCondition.create(
            CouponOnOrderCancel.KEEP,
            CouponOnPartialCancel.RECOVER_IF_UNUSED,
            CouponOnRefund.KEEP_DISCOUNT,
            false,
            null
        );

        assertThat(condition.recoversOnPartialCancel(true)).isTrue();
        assertThat(condition.recoversOnPartialCancel(false)).isFalse();
    }

    @Test
    @DisplayName("환불 시 RECALCULATE면 재계산한다")
    void recalculatesOnRefundWhenRecalculate() {
        final CouponLifecycleCondition condition = CouponLifecycleCondition.create(
            CouponOnOrderCancel.KEEP,
            CouponOnPartialCancel.KEEP,
            CouponOnRefund.RECALCULATE,
            true,
            null
        );

        assertThat(condition.recalculatesOnRefund()).isTrue();
        assertThat(condition.isReissuable()).isTrue();
    }
}
