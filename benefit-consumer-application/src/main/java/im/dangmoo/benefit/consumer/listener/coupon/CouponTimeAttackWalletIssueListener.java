package im.dangmoo.benefit.consumer.listener.coupon;

import im.dangmoo.benefit.consumer.KafkaConsumerTopics;
import im.dangmoo.benefit.consumer.handler.coupon.CouponTimeAttackWalletIssueHandler;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletIssueEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponTimeAttackWalletIssueListener {

    private final CouponTimeAttackWalletIssueHandler couponTimeAttackWalletIssueHandler;

    public CouponTimeAttackWalletIssueListener(
        final CouponTimeAttackWalletIssueHandler couponTimeAttackWalletIssueHandler
    ) {
        this.couponTimeAttackWalletIssueHandler = couponTimeAttackWalletIssueHandler;
    }

    @KafkaListener(topics = KafkaConsumerTopics.COUPON_TIME_ATTACK_WALLET_ISSUE, concurrency = "1")
    public void listen(final CouponWalletIssueEvent event) {
        if (event == null || event.walletId() == null || event.idempotencyKey() == null) {
            return;
        }
        couponTimeAttackWalletIssueHandler.handle(event);
    }
}
