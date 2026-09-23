package im.dangmoo.benefit.infrastructure.support.kafka;

public final class KafkaProducerTopics {

    public static final String BATCH_CENTER_JOB_TRIGGER = "batch.center.job.trigger";
    public static final String COUPON_POLICY_CHANGED = "benefit.coupon.policy.changed";
    public static final String COUPON_TIME_ATTACK_ISSUE = "benefit.coupon.time-attack.issue";

    private KafkaProducerTopics() {
    }
}
