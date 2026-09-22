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
