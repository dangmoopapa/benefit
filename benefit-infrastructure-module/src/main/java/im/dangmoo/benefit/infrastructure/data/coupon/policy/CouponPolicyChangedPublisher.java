package im.dangmoo.benefit.infrastructure.data.coupon.policy;

import im.dangmoo.benefit.infrastructure.collection.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponPolicyChangedPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CouponPolicyChangedPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CouponPolicyChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.COUPON_POLICY_CHANGED, event.policyId(), event);
    }
}
