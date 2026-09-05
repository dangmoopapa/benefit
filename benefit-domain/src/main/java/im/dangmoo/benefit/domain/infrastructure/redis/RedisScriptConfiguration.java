package im.dangmoo.benefit.domain.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfiguration {

    private static final String COUPON_STOCK_RESERVE = "redis/coupon_stock_reserve.lua";

    @Bean
    RedisScript<String> couponStockReserveScript() {
        return RedisScript.of(new ClassPathResource(COUPON_STOCK_RESERVE), String.class);
    }
}
