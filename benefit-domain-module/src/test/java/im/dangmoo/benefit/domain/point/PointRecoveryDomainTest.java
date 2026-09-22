package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointLifecycleCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PointRecoveryDomainTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("isReclaimable 은 lifecycle 조건을 그대로 반영한다")
    void isReclaimable(final boolean reclaimable) {
        final PointRecoveryDomain domain = PointRecoveryDomain.of(
            PointLifecycleCondition.create(reclaimable)
        );
        assertThat(domain.isReclaimable()).isEqualTo(reclaimable);
    }
}
