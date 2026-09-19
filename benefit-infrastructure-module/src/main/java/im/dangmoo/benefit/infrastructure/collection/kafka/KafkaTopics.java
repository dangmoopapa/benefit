package im.dangmoo.benefit.infrastructure.collection.kafka;

public final class KafkaTopics {

    public static final String BATCH_CENTER_JOB_TRIGGER = "batch.center.job.trigger";
    public static final String COUPON_TIME_ATTACK_WALLET_ISSUE = "benefit.coupon.time-attack.wallet.issue";
    public static final String COUPON_POLICY_CHANGED = "benefit.coupon.policy.changed";

    private KafkaTopics() {
    }
}
