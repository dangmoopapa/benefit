package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointAccountCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitType;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointExpireType;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointLifecycleCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointExpireDomainTest {

    private static final Instant NOW = Instant.parse("2026-06-15T00:00:00Z");
    private static final Instant PAST = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant FUTURE = Instant.parse("2026-12-01T00:00:00Z");

    @Test
    @DisplayName("NEVER 정책은 만료되지 않는 시각을 만료일로 준다")
    void expiresAt_never() {
        final PointExpireDomain pointExpire = PointExpireDomain.of(
            policyExpiring(PointExpireCondition.create(PointExpireType.NEVER, null, null)), NOW
        );
        assertThat(pointExpire.expiresAt()).isEqualTo(PointBalanceDocument.NEVER_EXPIRES_AT);
    }

    @Test
    @DisplayName("FIXED_AT 정책은 지정일을 UTC 00시 키로 정규화한다")
    void expiresAt_fixedAt() {
        final Instant fixedAt = Instant.parse("2026-03-15T15:30:00Z");
        final PointExpireDomain pointExpire = PointExpireDomain.of(
            policyExpiring(PointExpireCondition.create(PointExpireType.FIXED_AT, fixedAt, null)), NOW
        );
        assertThat(pointExpire.expiresAt()).isEqualTo(Instant.parse("2026-03-15T00:00:00Z"));
    }

    @Test
    @DisplayName("DAYS_AFTER_GRANT 정책은 지급일 키에 일수를 더한다")
    void expiresAt_daysAfterGrant() {
        final PointExpireDomain pointExpire = PointExpireDomain.of(
            policyExpiring(PointExpireCondition.create(PointExpireType.DAYS_AFTER_GRANT, null, 10)),
            Instant.parse("2026-01-01T15:30:00Z")
        );
        assertThat(pointExpire.expiresAt()).isEqualTo(Instant.parse("2026-01-11T00:00:00Z"));
    }

    @Test
    @DisplayName("만료 없음 표식과 빈 만료일만 만료되지 않는다")
    void neverExpires() {
        assertThat(PointExpireDomain.of(PointBalanceDocument.NEVER_EXPIRES_AT).neverExpires()).isTrue();
        assertThat(PointExpireDomain.of(null).neverExpires()).isTrue();
        assertThat(PointExpireDomain.of(PAST).neverExpires()).isFalse();
    }

    @Test
    @DisplayName("만료 없음 표식은 만료일 없음으로 내려준다")
    void expiresAtOrNull() {
        assertThat(PointExpireDomain.of(PointBalanceDocument.NEVER_EXPIRES_AT).expiresAtOrNull()).isNull();
        assertThat(PointExpireDomain.of(PAST).expiresAtOrNull()).isEqualTo(PAST);
    }

    @Test
    @DisplayName("만료일이 기준 시각을 지났으면 만료된 것이다")
    void isExpiredAt() {
        assertThat(PointExpireDomain.of(PAST).isExpiredAt(NOW)).isTrue();
        assertThat(PointExpireDomain.of(NOW).isExpiredAt(NOW)).isTrue();
        assertThat(PointExpireDomain.of(FUTURE).isExpiredAt(NOW)).isFalse();
        assertThat(PointExpireDomain.of(PointBalanceDocument.NEVER_EXPIRES_AT).isExpiredAt(NOW)).isFalse();
    }

    private static PointPolicyDocument policyExpiring(final PointExpireCondition expireCondition) {
        return PointPolicyDocument.create(
            "가입 축하 포인트",
            "가입 시 1회 지급",
            "user.join",
            PointBenefitCondition.create(PointBenefitType.FIXED, 100L, null, null, null, null),
            PointIssueCondition.create(null, null, null, List.of(), List.of()),
            expireCondition,
            PointLifecycleCondition.create(false),
            PointAccountCondition.create(null),
            "system"
        );
    }
}
