package im.dangmoo.benefit.infrastructure.data.coupon.policy.changed;

import im.dangmoo.benefit.infrastructure.support.kafka.KafkaProducerTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponPolicyChangedPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CouponPolicyChangedPublisher(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CouponPolicyChangedPublication publication) {
        kafkaTemplate.send(KafkaProducerTopics.COUPON_POLICY_CHANGED, publication.policyId(), publication);
    }
}
