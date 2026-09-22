package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrizeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromotionEntryDomainTest {

    @Test
    @DisplayName("이미 응모했으면 AlreadyAppliedException")
    void requireNotAlreadyApplied_alreadyApplied() {
        final PromotionEntryDomain domain = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.MANUAL,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        assertThatThrownBy(() -> domain.requireNotAlreadyApplied(true))
            .isInstanceOf(PromotionEntryDomain.AlreadyAppliedException.class);
        assertThatCode(() -> domain.requireNotAlreadyApplied(false)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("MANUAL 추첨만 requireManualLotteryReady 를 통과한다")
    void requireManualLotteryReady() {
        final PromotionEntryDomain manual = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.MANUAL,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        final PromotionEntryDomain auto = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );

        assertThatCode(() -> manual.requireManualLotteryReady(false)).doesNotThrowAnyException();
        assertThatThrownBy(() -> auto.requireManualLotteryReady(false))
            .isInstanceOf(PromotionEntryDomain.LotteryNotReadyException.class);
        assertThatThrownBy(() -> manual.requireManualLotteryReady(true))
            .isInstanceOf(PromotionEntryDomain.AlreadyDrawnException.class);
    }

    @Test
    @DisplayName("AUTO_COUNT 추첨만 requireAutoLotteryReady 를 통과한다")
    void requireAutoLotteryReady() {
        final PromotionEntryDomain auto = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        final PromotionEntryDomain manual = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.MANUAL,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );

        assertThatCode(() -> auto.requireAutoLotteryReady(false)).doesNotThrowAnyException();
        assertThatThrownBy(() -> manual.requireAutoLotteryReady(false))
            .isInstanceOf(PromotionEntryDomain.LotteryNotReadyException.class);
        assertThatThrownBy(() -> auto.requireAutoLotteryReady(true))
            .isInstanceOf(PromotionEntryDomain.AlreadyDrawnException.class);
    }

    @Test
    @DisplayName("이미 당첨된 유저는 제외하고 winnerCount 만큼 뽑는다")
    void selectWinners_filtersAndCaps() {
        final PromotionEntryDomain domain = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                2,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        final List<String> winners = domain.selectWinners(List.of("u1", "u2", "u3"), Set.of("u2"));
        assertThat(winners).hasSize(2).doesNotContain("u2").allMatch(id -> List.of("u1", "u3").contains(id));
    }

    @Test
    @DisplayName("후보가 없거나 winnerCount 가 비정상이면 빈 목록이다")
    void selectWinners_empty() {
        assertThat(PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                0,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        ).selectWinners(List.of("u1"), Set.of())).isEmpty();

        assertThat(PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                null,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        ).selectWinners(List.of("u1"), Set.of())).isEmpty();

        assertThat(PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                3,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        ).selectWinners(List.of("u1"), Set.of("u1"))).isEmpty();
    }

    @Test
    @DisplayName("후보가 winnerCount 보다 적으면 후보 수만큼만 뽑는다")
    void selectWinners_fewerThanWinnerCount() {
        final PromotionEntryDomain domain = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.AUTO_COUNT,
                5,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        assertThat(domain.selectWinners(List.of("u1", "u2"), Set.of())).hasSize(2);
    }

    @Test
    @DisplayName("lotteryType 과 prizes 를 노출한다")
    void accessors() {
        final PromotionEntryDomain domain = PromotionEntryDomain.of(
            PromotionEntry.create(
                null,
                PromotionLotteryType.MANUAL,
                1,
                List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "prize"))
            )
        );
        assertThat(domain.lotteryType()).isEqualTo(PromotionLotteryType.MANUAL);
        assertThat(domain.prizes()).hasSize(1);
    }
}
