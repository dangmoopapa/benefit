package im.dangmoo.benefit.infrastructure.data.promotion.feature;

public class PromotionLanding {

    private String buttonLabel;
    private String url;
    private PromotionLandingTarget target;

    private PromotionLanding() {
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public String getUrl() {
        return url;
    }

    public PromotionLandingTarget getTarget() {
        return target;
    }
}
