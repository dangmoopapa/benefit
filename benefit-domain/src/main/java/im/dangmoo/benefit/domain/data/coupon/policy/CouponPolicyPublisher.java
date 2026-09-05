package im.dangmoo.benefit.domain.data.coupon.policy;

import im.dangmoo.benefit.domain.infrastructure.kafka.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponPolicyPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CouponPolicyPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CouponPolicyChangedEvent event) {
        if (event.status() == CouponPolicyStatus.DRAFT) {
            return;
        }
        kafkaTemplate.send(KafkaTopics.COUPON_POLICY_CHANGED, event.policyId(), event);
    }
}
