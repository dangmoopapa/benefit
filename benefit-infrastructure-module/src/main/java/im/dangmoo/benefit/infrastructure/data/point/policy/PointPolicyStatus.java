package im.dangmoo.benefit.infrastructure.data.point.policy;

public enum PointPolicyStatus {
    DRAFT,
    ACTIVE,
    ENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isNotActive() {
        return !isActive();
    }
}
