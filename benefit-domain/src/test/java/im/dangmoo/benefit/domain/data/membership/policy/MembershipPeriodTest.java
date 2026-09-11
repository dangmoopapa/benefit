package im.dangmoo.benefit.domain.data.membership.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipPeriodTest {

    @Test
    @DisplayName("개월 수만큼 만료 시각을 더한다")
    void addsMonths() {
        final MembershipPeriod period = MembershipPeriod.create(1);
        final Instant startedAt = Instant.parse("2026-01-31T15:00:00Z");

        assertThat(period.resolveExpiresAt(startedAt))
            .isEqualTo(Instant.parse("2026-02-28T15:00:00Z"));
    }
}
