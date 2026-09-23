package im.dangmoo.benefit.data.infrastructure.coupon;

import im.dangmoo.benefit.data.infrastructure.KafkaProducerTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import im.dangmoo.benefit.data.entity.coupon.wallet.issue.*;

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
