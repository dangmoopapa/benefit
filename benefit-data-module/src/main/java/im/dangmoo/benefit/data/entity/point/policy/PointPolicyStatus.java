package im.dangmoo.benefit.data.entity.point.policy;

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
