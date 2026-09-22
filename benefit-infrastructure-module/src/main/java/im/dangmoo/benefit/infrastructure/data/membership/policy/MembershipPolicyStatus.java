package im.dangmoo.benefit.infrastructure.data.membership.policy;

public enum MembershipPolicyStatus {
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
