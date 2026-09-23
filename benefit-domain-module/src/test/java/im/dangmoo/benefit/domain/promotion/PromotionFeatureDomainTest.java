package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.data.entity.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionLanding;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionLandingTarget;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionPrizeType;
import im.dangmoo.benefit.data.entity.promotion.feature.PromotionProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionFeatureDomainTest {

    @Test
    @DisplayName("기능이 하나도 없으면 설정되지 않은 것이다")
    void isConfigured_empty() {
        assertThat(PromotionFeatureDomain.of(List.of()).isConfigured()).isFalse();
        assertThat(PromotionFeatureDomain.of(null).isConfigured()).isFalse();
    }

    @Test
    @DisplayName("기능 타입이 없으면 설정되지 않은 것이다")
    void isConfigured_nullType() {
        assertThat(configured(feature(null, List.of(), null, null, null, null))).isFalse();
        assertThat(PromotionFeatureDomain.of(Collections.singletonList(null)).isConfigured()).isFalse();
    }

    @Test
    @DisplayName("안내 기능은 추가 설정 없이도 완성이다")
    void isConfigured_info() {
        assertThat(configured(feature(PromotionFeatureType.INFO, List.of(), null, null, null, null)))
            .isTrue();
    }

    @Test
    @DisplayName("상품 기능은 상품이 있어야 완성이다")
    void isConfigured_products() {
        assertThat(configured(feature(
            PromotionFeatureType.PRODUCTS,
            List.of(PromotionProduct.create("p1", "상품", null, 0)),
            null, null, null, null
        ))).isTrue();
        assertThat(configured(feature(PromotionFeatureType.PRODUCTS, List.of(), null, null, null, null)))
            .isFalse();
    }

    @Test
    @DisplayName("랜딩 기능은 URL 과 타겟이 있어야 완성이다")
    void isConfigured_landing() {
        assertThat(configured(feature(
            PromotionFeatureType.LANDING,
            List.of(),
            PromotionLanding.create("바로가기", "https://dangmoo.im", PromotionLandingTarget.EXTERNAL),
            null, null, null
        ))).isTrue();
        assertThat(configured(feature(
            PromotionFeatureType.LANDING,
            List.of(),
            PromotionLanding.create("바로가기", " ", PromotionLandingTarget.EXTERNAL),
            null, null, null
        ))).isFalse();
        assertThat(configured(feature(
            PromotionFeatureType.LANDING,
            List.of(),
            PromotionLanding.create("바로가기", "https://dangmoo.im", null),
            null, null, null
        ))).isFalse();
        assertThat(configured(feature(PromotionFeatureType.LANDING, List.of(), null, null, null, null)))
            .isFalse();
    }

    @Test
    @DisplayName("쿠폰 발급 기능은 쿠폰 정책 키가 있어야 완성이다")
    void isConfigured_couponIssue() {
        assertThat(configured(feature(
            PromotionFeatureType.COUPON_ISSUE, List.of(), null, null, "coupon.welcome", null
        ))).isTrue();
        assertThat(configured(feature(PromotionFeatureType.COUPON_ISSUE, List.of(), null, null, null, null)))
            .isFalse();
    }

    @Test
    @DisplayName("포인트 지급 기능은 포인트 정책 키가 있어야 완성이다")
    void isConfigured_pointIssue() {
        assertThat(configured(feature(
            PromotionFeatureType.POINT_ISSUE, List.of(), null, null, null, "point.welcome"
        ))).isTrue();
        assertThat(configured(feature(PromotionFeatureType.POINT_ISSUE, List.of(), null, null, null, null)))
            .isFalse();
    }

    @Test
    @DisplayName("응모 기능은 추첨 방식과 경품이 있어야 완성이다")
    void isConfigured_entry() {
        assertThat(configured(entryFeature(PromotionLotteryType.MANUAL, null, textPrize()))).isTrue();
        assertThat(configured(entryFeature(PromotionLotteryType.AUTO_COUNT, 3, textPrize()))).isTrue();
    }

    @Test
    @DisplayName("추첨 방식이 없거나 경품이 없으면 응모 기능은 미완성이다")
    void isConfigured_entryMissing() {
        assertThat(configured(entryFeature(null, 3, textPrize()))).isFalse();
        assertThat(configured(feature(PromotionFeatureType.ENTRY, List.of(), null, null, null, null)))
            .isFalse();
        assertThat(PromotionFeatureDomain.of(List.of(PromotionFeature.create(
            PromotionFeatureType.ENTRY,
            List.of(),
            null,
            PromotionEntry.create("응모하기", PromotionLotteryType.MANUAL, null, List.of()),
            null,
            null,
            null
        ))).isConfigured()).isFalse();
    }

    @Test
    @DisplayName("자동 추첨은 당첨자 수가 1 이상이어야 완성이다")
    void isConfigured_entryWinnerCount() {
        assertThat(configured(entryFeature(PromotionLotteryType.AUTO_COUNT, null, textPrize()))).isFalse();
        assertThat(configured(entryFeature(PromotionLotteryType.AUTO_COUNT, 0, textPrize()))).isFalse();
    }

    @Test
    @DisplayName("경품은 타입별 지급 대상이 있어야 완성이다")
    void isConfigured_prizes() {
        assertThat(configured(entryFeature(
            PromotionLotteryType.MANUAL,
            null,
            PromotionPrize.create(PromotionPrizeType.COUPON, "coupon.welcome", null, null)
        ))).isTrue();
        assertThat(configured(entryFeature(
            PromotionLotteryType.MANUAL,
            null,
            PromotionPrize.create(PromotionPrizeType.POINT, null, "point.welcome", null)
        ))).isTrue();
        assertThat(configured(entryFeature(
            PromotionLotteryType.MANUAL,
            null,
            PromotionPrize.create(PromotionPrizeType.COUPON, null, null, null)
        ))).isFalse();
        assertThat(configured(entryFeature(
            PromotionLotteryType.MANUAL,
            null,
            PromotionPrize.create(PromotionPrizeType.TEXT, null, null, " ")
        ))).isFalse();
        assertThat(configured(entryFeature(
            PromotionLotteryType.MANUAL,
            null,
            PromotionPrize.create(null, null, null, "커피")
        ))).isFalse();
    }

    @Test
    @DisplayName("기능 중 하나라도 미완성이면 전체가 미완성이다")
    void isConfigured_anyIncomplete() {
        assertThat(PromotionFeatureDomain.of(List.of(
            feature(PromotionFeatureType.INFO, List.of(), null, null, null, null),
            feature(PromotionFeatureType.COUPON_ISSUE, List.of(), null, null, null, null)
        )).isConfigured()).isFalse();
    }

    private static boolean configured(final PromotionFeature feature) {
        return PromotionFeatureDomain.of(List.of(feature)).isConfigured();
    }

    private static PromotionFeature feature(
        final PromotionFeatureType type,
        final List<PromotionProduct> products,
        final PromotionLanding landing,
        final PromotionEntry entry,
        final String couponPolicyKey,
        final String pointPolicyKey
    ) {
        return PromotionFeature.create(type, products, landing, entry, null, couponPolicyKey, pointPolicyKey);
    }

    private static PromotionFeature entryFeature(
        final PromotionLotteryType lotteryType,
        final Integer winnerCount,
        final PromotionPrize prize
    ) {
        return feature(
            PromotionFeatureType.ENTRY,
            List.of(),
            null,
            PromotionEntry.create("응모하기", lotteryType, winnerCount, List.of(prize)),
            null,
            null
        );
    }

    private static PromotionPrize textPrize() {
        return PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "커피 기프티콘");
    }
}
