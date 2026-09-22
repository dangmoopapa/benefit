package im.dangmoo.benefit.infrastructure.data.promotion.feature;

import java.util.ArrayList;
import java.util.List;

public class PromotionFeature {

    private PromotionFeatureType type;
    private List<PromotionProduct> products = new ArrayList<>();
    private PromotionLanding landing;
    private PromotionEntry entry;
    private String buttonLabel;
    private String couponPolicyKey;
    private String pointPolicyKey;

    private PromotionFeature() {
    }

    public static PromotionFeature create(
        final PromotionFeatureType type,
        final List<PromotionProduct> products,
        final PromotionLanding landing,
        final PromotionEntry entry,
        final String buttonLabel,
        final String couponPolicyKey,
        final String pointPolicyKey
    ) {
        final PromotionFeature feature = new PromotionFeature();
        feature.type = type;
        feature.products = products == null ? new ArrayList<>() : new ArrayList<>(products);
        feature.landing = landing;
        feature.entry = entry;
        feature.buttonLabel = buttonLabel;
        feature.couponPolicyKey = couponPolicyKey;
        feature.pointPolicyKey = pointPolicyKey;
        return feature;
    }

    public PromotionFeatureType getType() {
        return type;
    }

    public List<PromotionProduct> getProducts() {
        return products;
    }

    public PromotionLanding getLanding() {
        return landing;
    }

    public PromotionEntry getEntry() {
        return entry;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getCouponPolicyKey() {
        return couponPolicyKey;
    }

    public String getPointPolicyKey() {
        return pointPolicyKey;
    }
}
