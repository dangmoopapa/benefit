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

public class PromotionFeatureDomain {

    private final List<Feature> features;

    private PromotionFeatureDomain(final List<Feature> features) {
        this.features = features;
    }

    public static PromotionFeatureDomain of(final List<PromotionFeature> features) {
        if (features == null) {
            return new PromotionFeatureDomain(List.of());
        }
        final List<Feature> extracted = new ArrayList<>();
        for (final PromotionFeature feature : features) {
            extracted.add(Feature.of(feature));
        }
        return new PromotionFeatureDomain(extracted);
    }

    public void requireReady() {
        if (features.isEmpty()) {
            throw new InvalidFeatureException();
        }
        for (final Feature feature : features) {
            feature.requireReady();
        }
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

        private void requireReady() {
            if (type == null) {
                throw new InvalidFeatureException();
            }
            switch (type) {
                case INFO -> {
                }
                case PRODUCTS -> {
                    if (!hasProducts) {
                        throw new InvalidFeatureException();
                    }
                }
                case LANDING -> {
                    if (!StringUtils.hasText(landingUrl) || landingTarget == null) {
                        throw new InvalidFeatureException();
                    }
                }
                case ENTRY -> requireReadyEntry();
                case COUPON_ISSUE -> {
                    if (!StringUtils.hasText(couponPolicyKey)) {
                        throw new InvalidFeatureException();
                    }
                }
                case POINT_ISSUE -> {
                    if (!StringUtils.hasText(pointPolicyKey)) {
                        throw new InvalidFeatureException();
                    }
                }
            }
        }

        private void requireReadyEntry() {
            if (lotteryType == null) {
                throw new InvalidFeatureException();
            }
            if (lotteryType == PromotionLotteryType.AUTO_COUNT
                && (winnerCount == null || winnerCount <= 0)) {
                throw new InvalidFeatureException();
            }
            if (prizes.isEmpty()) {
                throw new InvalidFeatureException();
            }
            for (final Prize prize : prizes) {
                prize.requireReady();
            }
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

        private void requireReady() {
            if (type == null) {
                throw new InvalidFeatureException();
            }
            if (type == PromotionPrizeType.COUPON && !StringUtils.hasText(couponPolicyKey)) {
                throw new InvalidFeatureException();
            }
            if (type == PromotionPrizeType.POINT && !StringUtils.hasText(pointPolicyKey)) {
                throw new InvalidFeatureException();
            }
            if (type == PromotionPrizeType.TEXT && !StringUtils.hasText(text)) {
                throw new InvalidFeatureException();
            }
        }
    }

    public static class InvalidFeatureException extends RuntimeException {
    }
}
