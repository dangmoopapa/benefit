package im.dangmoo.benefit.infrastructure.data.coupon.wallet.issue;

import im.dangmoo.benefit.infrastructure.support.kafka.KafkaProducerTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponTimeAttackIssuePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CouponTimeAttackIssuePublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CouponTimaAttackIssuePublication publication) {
        kafkaTemplate.send(KafkaProducerTopics.COUPON_TIME_ATTACK_ISSUE, publication.userId(), publication);
    }
}
