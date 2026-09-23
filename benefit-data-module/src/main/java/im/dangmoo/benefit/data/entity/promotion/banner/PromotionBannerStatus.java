package im.dangmoo.benefit.data.entity.promotion.banner;

public enum PromotionBannerStatus {
    DRAFT,
    ACTIVE,
    ENDED;

    public boolean isNotActive() {
        return this != ACTIVE;
    }
}
