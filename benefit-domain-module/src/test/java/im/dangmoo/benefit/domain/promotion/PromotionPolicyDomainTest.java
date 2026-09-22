package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrizeType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromotionPolicyDomainTest {

    @Test
    @DisplayName("기간 안이면 isInPeriod, 이후면 isEnded")
    void periodBounds() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final PromotionPolicy policy = PromotionPolicy.create(
            "promo-1", null, "title", null, start, end, null, null, List.of(), 0, "admin"
        );
        policy.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        final PromotionPolicyDomain domain = PromotionPolicyDomain.of(policy);

        assertThat(domain.isInPeriod(start)).isTrue();
        assertThat(domain.isInPeriod(end)).isTrue();
        assertThat(domain.isInPeriod(start.minusSeconds(1))).isFalse();
        assertThat(domain.isEnded(end)).isFalse();
        assertThat(domain.isEnded(end.plusSeconds(1))).isTrue();
    }

    @Test
    @DisplayName("isLive 는 ACTIVE 이고 기간 중일 때만 true")
    void isLive() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final Instant mid = Instant.parse("2026-01-15T00:00:00Z");

        final PromotionPolicy active = PromotionPolicy.create(
            "promo-1", null, "title", null, start, end, null, null, List.of(), 0, "admin"
        );
        active.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThat(PromotionPolicyDomain.of(active).isLive(mid)).isTrue();

        final PromotionPolicy draft = PromotionPolicy.create(
            "promo-2", null, "title", null, start, end, null, null, List.of(), 0, "admin"
        );
        assertThat(PromotionPolicyDomain.of(draft).isLive(mid)).isFalse();

        assertThat(PromotionPolicyDomain.of(active).isLive(end.plusSeconds(1))).isFalse();
    }

    @Test
    @DisplayName("ENTRY feature 가 있으면 hasEntry")
    void hasEntry() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");

        final PromotionPolicy withEntry = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.AUTO_COUNT,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        withEntry.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThat(PromotionPolicyDomain.of(withEntry).hasEntry()).isTrue();
        assertThat(PromotionPolicyDomain.of(withEntry).entry()).isPresent();

        final PromotionPolicy without = PromotionPolicy.create(
            "promo-2", null, "title", null, start, end, null, null,
            List.of(PromotionFeature.create(PromotionFeatureType.INFO, null, null, null, null, null, null)),
            0,
            "admin"
        );
        without.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThat(PromotionPolicyDomain.of(without).hasEntry()).isFalse();
        assertThat(PromotionPolicyDomain.of(without).entry()).isEmpty();
    }

    @Test
    @DisplayName("라이브 + entry + 미응모면 requireApplicable 통과")
    void requireApplicable_ok() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final PromotionPolicy policy = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        policy.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatCode(() -> PromotionPolicyDomain.of(policy)
            .requireApplicable(Instant.parse("2026-01-15T00:00:00Z"), false))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("비라이브 또는 entry 없으면 NotApplicableException")
    void requireApplicable_notApplicable() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final Instant mid = Instant.parse("2026-01-15T00:00:00Z");

        final PromotionPolicy draft = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        assertThatThrownBy(() -> PromotionPolicyDomain.of(draft).requireApplicable(mid, false))
            .isInstanceOf(PromotionPolicyDomain.NotApplicableException.class);

        final PromotionPolicy noEntry = PromotionPolicy.create(
            "promo-2", null, "title", null, start, end, null, null,
            List.of(PromotionFeature.create(PromotionFeatureType.INFO, null, null, null, null, null, null)),
            0,
            "admin"
        );
        noEntry.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatThrownBy(() -> PromotionPolicyDomain.of(noEntry).requireApplicable(mid, false))
            .isInstanceOf(PromotionPolicyDomain.NotApplicableException.class);
    }

    @Test
    @DisplayName("이미 응모했으면 AlreadyAppliedException")
    void requireApplicable_alreadyApplied() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final PromotionPolicy policy = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        policy.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatThrownBy(() -> PromotionPolicyDomain.of(policy)
            .requireApplicable(Instant.parse("2026-01-15T00:00:00Z"), true))
            .isInstanceOf(PromotionEntryDomain.AlreadyAppliedException.class);
    }

    @Test
    @DisplayName("종료 + AUTO_COUNT 이면 requireAutoLotteryReady 통과")
    void requireAutoLotteryReady_ok() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final PromotionPolicy policy = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.AUTO_COUNT,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        policy.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatCode(() -> PromotionPolicyDomain.of(policy).requireAutoLotteryReady(end.plusSeconds(1), false))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("아직 종료 전이거나 entry 없으면 LotteryNotReadyException")
    void requireAutoLotteryReady_notReady() {
        final Instant start = Instant.parse("2026-01-01T00:00:00Z");
        final Instant end = Instant.parse("2026-01-31T00:00:00Z");
        final Instant mid = Instant.parse("2026-01-15T00:00:00Z");

        final PromotionPolicy withEntry = PromotionPolicy.create(
            "promo-1",
            null,
            "title",
            null,
            start,
            end,
            null,
            null,
            List.of(PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.AUTO_COUNT,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
                ),
                null,
                null,
                null
            )),
            0,
            "admin"
        );
        withEntry.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatThrownBy(() -> PromotionPolicyDomain.of(withEntry).requireAutoLotteryReady(mid, false))
            .isInstanceOf(PromotionPolicyDomain.LotteryNotReadyException.class);

        final PromotionPolicy noEntry = PromotionPolicy.create(
            "promo-2", null, "title", null, start, end, null, null,
            List.of(PromotionFeature.create(PromotionFeatureType.INFO, null, null, null, null, null, null)),
            0,
            "admin"
        );
        noEntry.changeStatus(PromotionPolicyStatus.ACTIVE, "admin");
        assertThatThrownBy(() -> PromotionPolicyDomain.of(noEntry).requireAutoLotteryReady(end.plusSeconds(1), false))
            .isInstanceOf(PromotionPolicyDomain.LotteryNotReadyException.class);
    }
}
