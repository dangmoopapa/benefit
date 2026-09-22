package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLanding;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLandingTarget;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrizeType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromotionFeatureDomainTest {

    @Test
    @DisplayName("features 가 null 또는 비어 있으면 InvalidFeatureException")
    void emptyOrNull() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(null).requireReady())
            .isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of()).requireReady())
            .isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);
    }

    @Test
    @DisplayName("INFO 는 추가 필드 없이 통과한다")
    void info_ok() {
        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.INFO, null, null, null, null, null, null)
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("PRODUCTS 는 상품이 있어야 한다")
    void products() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.PRODUCTS, List.of(), null, null, null, null, null)
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.PRODUCTS,
                List.of(PromotionProduct.create("p1", "name", null, 0)),
                null,
                null,
                null,
                null,
                null
            )
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("LANDING 은 url 과 target 이 있어야 한다")
    void landing() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.LANDING, null, null, null, null, null, null)
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.LANDING,
                null,
                PromotionLanding.create(null, "https://example.com", PromotionLandingTarget.EXTERNAL),
                null,
                null,
                null,
                null
            )
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("COUPON_ISSUE / POINT_ISSUE 는 정책 키가 있어야 한다")
    void issueKeys() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.COUPON_ISSUE, null, null, null, null, null, null)
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.COUPON_ISSUE, null, null, null, null, "coupon-1", null)
        )).requireReady()).doesNotThrowAnyException();

        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.POINT_ISSUE, null, null, null, null, null, null)
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(PromotionFeatureType.POINT_ISSUE, null, null, null, null, null, "point-1")
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ENTRY AUTO_COUNT 는 winnerCount>0 과 prizes 가 필요하다")
    void entryAutoCount() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.AUTO_COUNT,
                    0,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "t"))
                ),
                null,
                null,
                null
            )
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(null, PromotionLotteryType.AUTO_COUNT, 1, List.of()),
                null,
                null,
                null
            )
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.AUTO_COUNT,
                    1,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "t"))
                ),
                null,
                null,
                null
            )
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ENTRY MANUAL 은 winnerCount 없이 prizes 만 있으면 통과한다")
    void entryManual() {
        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    null,
                    List.of(PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "t"))
                ),
                null,
                null,
                null
            )
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("prize 타입별 필수 필드를 검증한다")
    void prizeFields() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    null,
                    List.of(PromotionPrize.create(PromotionPrizeType.COUPON, null, null, null))
                ),
                null,
                null,
                null
            )
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    null,
                    List.of(PromotionPrize.create(PromotionPrizeType.POINT, null, null, null))
                ),
                null,
                null,
                null
            )
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);

        assertThatCode(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(
                PromotionFeatureType.ENTRY,
                null,
                null,
                PromotionEntry.create(
                    null,
                    PromotionLotteryType.MANUAL,
                    null,
                    List.of(
                        PromotionPrize.create(PromotionPrizeType.COUPON, "c1", null, null),
                        PromotionPrize.create(PromotionPrizeType.POINT, null, "p1", null),
                        PromotionPrize.create(PromotionPrizeType.TEXT, null, null, "hello")
                    )
                ),
                null,
                null,
                null
            )
        )).requireReady()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("type 이 null 이면 InvalidFeatureException")
    void nullType() {
        assertThatThrownBy(() -> PromotionFeatureDomain.of(List.of(
            PromotionFeature.create(null, null, null, null, null, null, null)
        )).requireReady()).isInstanceOf(PromotionFeatureDomain.InvalidFeatureException.class);
    }
}
