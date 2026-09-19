package im.dangmoo.benefit.infrastructure.data.coupon.wallet;

import im.dangmoo.benefit.infrastructure.collection.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TimeAttackCouponWalletIssuePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TimeAttackCouponWalletIssuePublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CouponWalletIssueEvent event) {
        kafkaTemplate.send(KafkaTopics.COUPON_TIME_ATTACK_WALLET_ISSUE, event.userId(), event);
    }
}
