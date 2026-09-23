package im.dangmoo.benefit.data.entity.point.policy.condition;

public class PointLifecycleCondition {

    private boolean reclaimable;

    private PointLifecycleCondition() {
    }

    public static PointLifecycleCondition create(final boolean reclaimable) {
        final PointLifecycleCondition condition = new PointLifecycleCondition();
        condition.reclaimable = reclaimable;
        return condition;
    }

    public boolean isReclaimable() {
        return reclaimable;
    }
}
