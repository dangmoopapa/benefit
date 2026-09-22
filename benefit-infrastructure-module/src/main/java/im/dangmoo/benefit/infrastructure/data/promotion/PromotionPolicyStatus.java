package im.dangmoo.benefit.infrastructure.data.promotion;

public enum PromotionPolicyStatus {
    DRAFT,
    ACTIVE,
    ENDED;

    public boolean isNotActive() {
        return this != ACTIVE;
    }
}
