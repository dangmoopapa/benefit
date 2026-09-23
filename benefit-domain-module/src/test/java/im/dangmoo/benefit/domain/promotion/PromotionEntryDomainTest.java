package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrizeType;
import im.dangmoo.benefit.infrastructure.data.promotion.policy.PromotionPolicyDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionEntryDomainTest {

    @Test
    @DisplayName("응모 기능이 있는 프로모션에서는 응모를 찾는다")
    void findIn_present() {
        final PromotionPolicyDocument policy = policyWith(entryFeature(PromotionLotteryType.MANUAL, 1));
        assertThat(PromotionEntryDomain.findIn(policy)).isPresent();
    }

    @Test
    @DisplayName("응모 기능이 없는 프로모션에서는 응모를 찾지 못한다")
    void findIn_absent() {
        final PromotionFeature infoFeature = PromotionFeature.create(
            PromotionFeatureType.INFO, List.of(), null, null, null, null, null
        );
        assertThat(PromotionEntryDomain.findIn(policyWith(infoFeature))).isEmpty();
    }

    @Test
    @DisplayName("응모 기능 타입이지만 응모 설정이 비어 있으면 응모를 찾지 못한다")
    void findIn_emptyEntry() {
        final PromotionFeature emptyEntryFeature = PromotionFeature.create(
            PromotionFeatureType.ENTRY, List.of(), null, null, "응모하기", null, null
        );
        assertThat(PromotionEntryDomain.findIn(policyWith(emptyEntryFeature))).isEmpty();
    }

    @Test
    @DisplayName("수동 추첨 응모는 수동으로 뽑을 수 있다")
    void manualDrawability_drawable() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.MANUAL, 1);
        assertThat(promotionEntry.manualDrawability(false))
            .isEqualTo(PromotionEntryDomain.Drawability.DRAWABLE);
    }

    @Test
    @DisplayName("자동 추첨 응모를 수동으로 뽑으면 LOTTERY_TYPE_MISMATCH")
    void manualDrawability_lotteryTypeMismatch() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 1);
        assertThat(promotionEntry.manualDrawability(false))
            .isEqualTo(PromotionEntryDomain.Drawability.LOTTERY_TYPE_MISMATCH);
    }

    @Test
    @DisplayName("이미 추첨했으면 ALREADY_DRAWN")
    void manualDrawability_alreadyDrawn() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.MANUAL, 1);
        assertThat(promotionEntry.manualDrawability(true))
            .isEqualTo(PromotionEntryDomain.Drawability.ALREADY_DRAWN);
    }

    @Test
    @DisplayName("자동 추첨 응모는 자동으로 뽑을 수 있다")
    void autoDrawability_drawable() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 1);
        assertThat(promotionEntry.autoDrawability(false))
            .isEqualTo(PromotionEntryDomain.Drawability.DRAWABLE);
    }

    @Test
    @DisplayName("수동 추첨 응모를 자동으로 뽑으면 LOTTERY_TYPE_MISMATCH")
    void autoDrawability_lotteryTypeMismatch() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.MANUAL, 1);
        assertThat(promotionEntry.autoDrawability(false))
            .isEqualTo(PromotionEntryDomain.Drawability.LOTTERY_TYPE_MISMATCH);
    }

    @Test
    @DisplayName("자동 추첨도 이미 뽑았으면 ALREADY_DRAWN")
    void autoDrawability_alreadyDrawn() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 1);
        assertThat(promotionEntry.autoDrawability(true))
            .isEqualTo(PromotionEntryDomain.Drawability.ALREADY_DRAWN);
    }

    @Test
    @DisplayName("경품 목록을 그대로 노출한다")
    void prizes() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.MANUAL, 1);
        assertThat(promotionEntry.prizes()).hasSize(1);
        assertThat(promotionEntry.prizes().getFirst().getType()).isEqualTo(PromotionPrizeType.TEXT);
    }

    @Test
    @DisplayName("당첨자 수만큼 이미 당첨되지 않은 응모자 중에서 뽑는다")
    void drawWinnersFrom_picksWinnerCount() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 2);
        final List<String> winnerUserIds = promotionEntry.drawWinnersFrom(
            List.of("u1", "u2", "u3", "u4"),
            Set.of("u1")
        );
        assertThat(winnerUserIds).hasSize(2).doesNotContain("u1").isSubsetOf("u2", "u3", "u4");
    }

    @Test
    @DisplayName("응모자가 당첨자 수보다 적으면 남은 응모자만 뽑는다")
    void drawWinnersFrom_fewerCandidates() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 5);
        assertThat(promotionEntry.drawWinnersFrom(List.of("u1", "u2"), Set.of()))
            .containsExactlyInAnyOrder("u1", "u2");
    }

    @Test
    @DisplayName("뽑을 응모자가 없으면 당첨자도 없다")
    void drawWinnersFrom_noCandidates() {
        final PromotionEntryDomain promotionEntry = entry(PromotionLotteryType.AUTO_COUNT, 2);
        assertThat(promotionEntry.drawWinnersFrom(List.of("u1"), Set.of("u1"))).isEmpty();
    }

    @Test
    @DisplayName("당첨자 수가 없거나 0 이하면 당첨자도 없다")
    void drawWinnersFrom_noWinnerCount() {
        assertThat(entry(PromotionLotteryType.AUTO_COUNT, null).drawWinnersFrom(List.of("u1"), Set.of()))
            .isEmpty();
        assertThat(entry(PromotionLotteryType.AUTO_COUNT, 0).drawWinnersFrom(List.of("u1"), Set.of()))
            .isEmpty();
    }

    private static PromotionEntryDomain entry(
        final PromotionLotteryType lotteryType,
        final Integer winnerCount
    ) {
        return PromotionEntryDomain.of(promotionEntry(lotteryType, winnerCount));
    }

    private static PromotionFeature entryFeature(
        final PromotionLotteryType lotteryType,
        final Integer winnerCount
    ) {
        return PromotionFeature.create(
            PromotionFeatureType.ENTRY,
            List.of(),
            null,
            promotionEntry(lotteryType, winnerCount),
            "응모하기",
            null,
            null
        );
    }

    private static PromotionEntry promotionEntry(
        final PromotionLotteryType lotteryType,
        final Integer winnerCount
    ) {
        return PromotionEntry.create(
            "응모하기",
            lotteryType,
            winnerCount,
            List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "커피 기프티콘"))
        );
    }

    private static PromotionPolicyDocument policyWith(final PromotionFeature feature) {
        return PromotionPolicyDocument.create(
            "summer-event",
            null,
            "여름 이벤트",
            "여름맞이 응모 이벤트",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T00:00:00Z"),
            null,
            null,
            List.of(feature),
            0,
            "system"
        );
    }
}
