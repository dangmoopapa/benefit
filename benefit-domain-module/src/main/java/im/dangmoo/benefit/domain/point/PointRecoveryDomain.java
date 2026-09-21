package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.policy.condition.PointLifecycleCondition;

public class PointRecoveryDomain {

    private final boolean reclaimable;

    private PointRecoveryDomain(final boolean reclaimable) {
        this.reclaimable = reclaimable;
    }

    public static PointRecoveryDomain of(final PointLifecycleCondition condition) {
        return new PointRecoveryDomain(condition.isReclaimable());
    }

    public boolean isReclaimable() {
        return reclaimable;
    }
}
