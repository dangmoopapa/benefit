package im.dangmoo.benefit.domain.coupon.data.stock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
class CouponStockConfiguration {

    @Bean
    RedisScript<String> couponStockReserveScript() {
        return RedisScript.of(new ClassPathResource("redis/coupon_stock_reserve.lua"), String.class);
    }
}
