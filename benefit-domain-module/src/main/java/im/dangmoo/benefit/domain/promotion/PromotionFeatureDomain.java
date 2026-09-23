package im.dangmoo.benefit.domain.promotion;

import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionEntry;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeatureType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLanding;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLandingTarget;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrizeType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PromotionFeatureDomain {

    private final List<Feature> features;

    private PromotionFeatureDomain(final List<Feature> features) {
        this.features = features;
    }

    public static PromotionFeatureDomain of(final List<PromotionFeature> features) {
        if (features == null) {
            return new PromotionFeatureDomain(List.of());
        }
        final List<Feature> configured = new ArrayList<>();
        for (final PromotionFeature feature : features) {
            configured.add(Feature.of(feature));
        }
        return new PromotionFeatureDomain(configured);
    }

    public boolean isConfigured() {
        return !features.isEmpty() && features.stream().allMatch(Feature::isConfigured);
    }

    private record Feature(
        PromotionFeatureType type,
        boolean hasProducts,
        String landingUrl,
        PromotionLandingTarget landingTarget,
        PromotionLotteryType lotteryType,
        Integer winnerCount,
        List<Prize> prizes,
        String couponPolicyKey,
        String pointPolicyKey
    ) {
        private static Feature of(final PromotionFeature feature) {
            if (feature == null) {
                return new Feature(null, false, null, null, null, null, List.of(), null, null);
            }
            final PromotionLanding landing = feature.getLanding();
            final PromotionEntry entry = feature.getEntry();
            final List<Prize> prizes = new ArrayList<>();
            if (entry != null && entry.getPrizes() != null) {
                for (final PromotionPrize prize : entry.getPrizes()) {
                    prizes.add(Prize.of(prize));
                }
            }
            return new Feature(
                feature.getType(),
                feature.getProducts() != null && !feature.getProducts().isEmpty(),
                landing == null ? null : landing.getUrl(),
                landing == null ? null : landing.getTarget(),
                entry == null ? null : entry.getLotteryType(),
                entry == null ? null : entry.getWinnerCount(),
                prizes,
                feature.getCouponPolicyKey(),
                feature.getPointPolicyKey()
            );
        }

        private boolean isConfigured() {
            if (type == null) {
                return false;
            }
            return switch (type) {
                case INFO -> true;
                case PRODUCTS -> hasProducts;
                case LANDING -> StringUtils.hasText(landingUrl) && landingTarget != null;
                case ENTRY -> isEntryConfigured();
                case COUPON_ISSUE -> StringUtils.hasText(couponPolicyKey);
                case POINT_ISSUE -> StringUtils.hasText(pointPolicyKey);
            };
        }

        private boolean isEntryConfigured() {
            if (lotteryType == null) {
                return false;
            }
            if (lotteryType == PromotionLotteryType.AUTO_COUNT
                && (winnerCount == null || winnerCount <= 0)) {
                return false;
            }
            return !prizes.isEmpty() && prizes.stream().allMatch(Prize::isConfigured);
        }
    }

    private record Prize(
        PromotionPrizeType type,
        String couponPolicyKey,
        String pointPolicyKey,
        String text
    ) {
        private static Prize of(final PromotionPrize prize) {
            if (prize == null) {
                return new Prize(null, null, null, null);
            }
            return new Prize(
                prize.getType(),
                prize.getCouponPolicyKey(),
                prize.getPointPolicyKey(),
                prize.getText()
            );
        }

        private boolean isConfigured() {
            if (type == null) {
                return false;
            }
            return switch (type) {
                case COUPON -> StringUtils.hasText(couponPolicyKey);
                case POINT -> StringUtils.hasText(pointPolicyKey);
                case TEXT -> StringUtils.hasText(text);
            };
        }
    }
}
