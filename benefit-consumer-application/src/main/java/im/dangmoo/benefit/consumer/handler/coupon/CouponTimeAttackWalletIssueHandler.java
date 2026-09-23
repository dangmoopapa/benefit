package im.dangmoo.benefit.consumer.handler.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletIssueEvent;
import im.dangmoo.benefit.infrastructure.data.coupon.wallet.CouponWalletMongoRepository;
import org.springframework.stereotype.Component;

@Component
public class CouponTimeAttackWalletIssueHandler {

    private final CouponWalletMongoRepository couponWalletMongoRepository;

    public CouponTimeAttackWalletIssueHandler(final CouponWalletMongoRepository couponWalletMongoRepository) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
    }

    public void handle(final CouponWalletIssueEvent event) {
        couponWalletMongoRepository.insert(event.toWallet());
    }
}
