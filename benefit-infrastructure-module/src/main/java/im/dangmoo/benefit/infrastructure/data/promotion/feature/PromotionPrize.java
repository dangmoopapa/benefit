package im.dangmoo.benefit.infrastructure.data.promotion.feature;

public class PromotionPrize {

    private PromotionPrizeType type;
    private String couponPolicyKey;
    private String pointPolicyKey;
    private String text;

    private PromotionPrize() {
    }

    public PromotionPrizeType getType() {
        return type;
    }

    public String getCouponPolicyKey() {
        return couponPolicyKey;
    }

    public String getPointPolicyKey() {
        return pointPolicyKey;
    }

    public String getText() {
        return text;
    }
}
