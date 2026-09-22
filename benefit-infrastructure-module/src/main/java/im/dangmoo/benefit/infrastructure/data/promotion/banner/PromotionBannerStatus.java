package im.dangmoo.benefit.infrastructure.data.promotion.banner;

public enum PromotionBannerStatus {
    DRAFT,
    ACTIVE,
    ENDED;

    public boolean isNotActive() {
        return this != ACTIVE;
    }
}
