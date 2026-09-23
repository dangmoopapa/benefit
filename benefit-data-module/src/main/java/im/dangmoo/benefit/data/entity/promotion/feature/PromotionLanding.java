package im.dangmoo.benefit.data.entity.promotion.feature;

public class PromotionLanding {

    private String buttonLabel;
    private String url;
    private PromotionLandingTarget target;

    private PromotionLanding() {
    }

    public static PromotionLanding create(
        final String buttonLabel,
        final String url,
        final PromotionLandingTarget target
    ) {
        final PromotionLanding landing = new PromotionLanding();
        landing.buttonLabel = buttonLabel;
        landing.url = url;
        landing.target = target;
        return landing;
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
