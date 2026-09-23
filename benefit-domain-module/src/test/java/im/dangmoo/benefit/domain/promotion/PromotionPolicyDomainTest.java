package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.data.entity.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.data.entity.promotion.policy.PromotionPolicyDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionPolicyDomainTest {

    private static final Instant START_AT = Instant.parse("2026-06-01T00:00:00Z");
    private static final Instant END_AT = Instant.parse("2026-06-30T00:00:00Z");
    private static final Instant DURING = Instant.parse("2026-06-15T00:00:00Z");

    @Test
    @DisplayName("활성 상태이고 기간 안이면 열려 있다")
    void isOpenAt_open() {
        final PromotionPolicyDomain promotion = PromotionPolicyDomain.of(activePolicy());
        assertThat(promotion.isOpenAt(DURING)).isTrue();
        assertThat(promotion.isOpenAt(START_AT)).isTrue();
        assertThat(promotion.isOpenAt(END_AT)).isTrue();
    }

    @Test
    @DisplayName("기간 밖이면 닫혀 있다")
    void isOpenAt_outOfPeriod() {
        final PromotionPolicyDomain promotion = PromotionPolicyDomain.of(activePolicy());
        assertThat(promotion.isOpenAt(START_AT.minusSeconds(1))).isFalse();
        assertThat(promotion.isOpenAt(END_AT.plusSeconds(1))).isFalse();
    }

    @Test
    @DisplayName("활성 상태가 아니면 기간 안이어도 닫혀 있다")
    void isOpenAt_notActive() {
        assertThat(PromotionPolicyDomain.of(draftPolicy()).isOpenAt(DURING)).isFalse();
    }

    @Test
    @DisplayName("종료 시각을 지나야 종료된 것이다")
    void isEndedAt() {
        final PromotionPolicyDomain promotion = PromotionPolicyDomain.of(activePolicy());
        assertThat(promotion.isEndedAt(END_AT)).isFalse();
        assertThat(promotion.isEndedAt(END_AT.plusSeconds(1))).isTrue();
    }

    @Test
    @DisplayName("열려 있고 아직 응모하지 않았으면 APPLICABLE")
    void applicabilityAt_applicable() {
        assertThat(PromotionPolicyDomain.of(activePolicy()).applicabilityAt(DURING, false))
            .isEqualTo(PromotionPolicyDomain.Applicability.APPLICABLE);
    }

    @Test
    @DisplayName("열려 있지 않으면 NOT_OPEN")
    void applicabilityAt_notOpen() {
        final PromotionPolicyDomain promotion = PromotionPolicyDomain.of(activePolicy());
        assertThat(promotion.applicabilityAt(END_AT.plusSeconds(1), false))
            .isEqualTo(PromotionPolicyDomain.Applicability.NOT_OPEN);
        assertThat(PromotionPolicyDomain.of(draftPolicy()).applicabilityAt(DURING, false))
            .isEqualTo(PromotionPolicyDomain.Applicability.NOT_OPEN);
    }

    @Test
    @DisplayName("이미 응모했으면 ALREADY_APPLIED")
    void applicabilityAt_alreadyApplied() {
        assertThat(PromotionPolicyDomain.of(activePolicy()).applicabilityAt(DURING, true))
            .isEqualTo(PromotionPolicyDomain.Applicability.ALREADY_APPLIED);
    }

    private static PromotionPolicyDocument activePolicy() {
        return draftPolicy().changeStatus(PromotionPolicyStatus.ACTIVE, "system");
    }

    private static PromotionPolicyDocument draftPolicy() {
        return PromotionPolicyDocument.create(
            "summer-event",
            null,
            "여름 이벤트",
            "여름맞이 응모 이벤트",
            START_AT,
            END_AT,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.INFO, List.of(), null, null, null, null, null
            )),
            0,
            "system"
        );
    }
}
