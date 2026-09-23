package im.dangmoo.benefit.consumer.listener;

import im.dangmoo.benefit.consumer.consumption.CouponTimeAttackIssueConsumption;
import im.dangmoo.benefit.consumer.handler.CouponTimeAttackIssueHandler;
import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponTimeAttackIssueListener {

    private final CouponTimeAttackIssueHandler couponTimeAttackIssueHandler;

    public CouponTimeAttackIssueListener(
        final CouponTimeAttackIssueHandler couponTimeAttackIssueHandler
    ) {
        this.couponTimeAttackIssueHandler = couponTimeAttackIssueHandler;
    }

    @KafkaListener(topics = KafkaConsumerTopics.COUPON_TIME_ATTACK_ISSUE, concurrency = "1")
    public void listen(final CouponTimeAttackIssueConsumption consumption) {
        if (consumption == null || consumption.walletId() == null || consumption.idempotencyKey() == null) {
            return;
        }
        couponTimeAttackIssueHandler.handle(consumption);
    }
}
