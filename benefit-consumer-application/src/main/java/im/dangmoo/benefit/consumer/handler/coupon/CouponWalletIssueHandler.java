package im.dangmoo.benefit.consumer.handler.coupon;

import im.dangmoo.benefit.consumer.support.KafkaConsumerTopics;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletIssueEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CouponWalletIssueHandler {

    private final CouponWalletMongoRepository couponWalletMongoRepository;

    public CouponWalletIssueHandler(final CouponWalletMongoRepository couponWalletMongoRepository) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
    }

    @KafkaListener(topics = KafkaConsumerTopics.COUPON_TIME_ATTACK_WALLET_ISSUE, concurrency = "1")
    public void handle(final CouponWalletIssueEvent event) {
        if (event == null || event.walletId() == null) {
            return;
        }
        couponWalletMongoRepository.insert(event.toWallet());
    }
}
