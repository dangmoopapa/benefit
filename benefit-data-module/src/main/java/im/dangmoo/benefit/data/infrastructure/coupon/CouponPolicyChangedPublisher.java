package im.dangmoo.benefit.data.infrastructure.coupon;

import im.dangmoo.benefit.data.infrastructure.KafkaProducerTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import im.dangmoo.benefit.data.entity.coupon.policy.changed.*;

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
