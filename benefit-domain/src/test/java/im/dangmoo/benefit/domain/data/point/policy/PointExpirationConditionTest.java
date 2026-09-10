package im.dangmoo.benefit.domain.data.point.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PointExpirationConditionTest {

    private static final Instant ISSUED_AT = Instant.parse("2026-03-15T01:30:00Z");

    @Test
    @DisplayName("NONE 이면 만료 없음")
    void noneNeverExpires() {
        final PointExpirationCondition expiration = PointExpirationCondition.create(
            PointExpirationType.NONE,
            null,
            null,
            null
        );

        assertThat(expiration.resolveExpiresAt(ISSUED_AT)).isNull();
    }

    @Test
    @DisplayName("FIXED_END 는 종료 시각을 쓴다")
    void fixedEnd() {
        final Instant end = Instant.parse("2026-04-30T05:00:00Z");
        final PointExpirationCondition expiration = PointExpirationCondition.create(
            PointExpirationType.FIXED_END,
            null,
            null,
            end
        );

        assertThat(expiration.resolveExpiresAt(ISSUED_AT)).isEqualTo(end);
    }

    @Test
    @DisplayName("DURATION 은 지급 시각에 일/시간을 더한다")
    void durationFromIssuedAt() {
        final PointExpirationCondition expiration = PointExpirationCondition.create(
            PointExpirationType.DURATION,
            1,
            2,
            null
        );

        assertThat(expiration.resolveExpiresAt(ISSUED_AT))
            .isEqualTo(Instant.parse("2026-03-16T03:30:00Z"));
    }
}
