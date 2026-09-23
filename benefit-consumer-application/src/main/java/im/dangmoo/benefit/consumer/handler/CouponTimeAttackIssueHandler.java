package im.dangmoo.benefit.consumer.handler;

import im.dangmoo.benefit.consumer.consumption.CouponTimeAttackIssueConsumption;
import im.dangmoo.benefit.data.infrastructure.coupon.CouponWalletMongoRepository;
import org.springframework.stereotype.Component;

@Component
public class CouponTimeAttackIssueHandler {

    private final CouponWalletMongoRepository couponWalletMongoRepository;

    public CouponTimeAttackIssueHandler(final CouponWalletMongoRepository couponWalletMongoRepository) {
        this.couponWalletMongoRepository = couponWalletMongoRepository;
    }

    public void handle(final CouponTimeAttackIssueConsumption consumption) {
        couponWalletMongoRepository.insert(consumption.toWallet());
    }
}
