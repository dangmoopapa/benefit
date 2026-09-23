package im.dangmoo.benefit.data.entity.promotion.feature;

public class PromotionPrize {

    private PromotionPrizeType type;
    private String couponPolicyKey;
    private String pointPolicyKey;
    private String text;

    private PromotionPrize() {
    }

    public static PromotionPrize create(
        final PromotionPrizeType type,
        final String couponPolicyKey,
        final String pointPolicyKey,
        final String text
    ) {
        final PromotionPrize prize = new PromotionPrize();
        prize.type = type;
        prize.couponPolicyKey = couponPolicyKey;
        prize.pointPolicyKey = pointPolicyKey;
        prize.text = text;
        return prize;
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
