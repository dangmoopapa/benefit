package im.dangmoo.benefit.domain.infrastructure.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfiguration {

    private static final String COUPON_STOCK_RESERVE = "redis/coupon_stock_reserve.lua";
    private static final String COUPON_USAGE_CONSUME = "redis/coupon_usage_consume.lua";
    private static final String COUPON_USAGE_RELEASE = "redis/coupon_usage_release.lua";

    @Bean
    RedisScript<String> couponStockReserveScript() {
        return RedisScript.of(new ClassPathResource(COUPON_STOCK_RESERVE), String.class);
    }

    @Bean
    RedisScript<Long> couponUsageConsumeScript() {
        return RedisScript.of(new ClassPathResource(COUPON_USAGE_CONSUME), Long.class);
    }

    @Bean
    RedisScript<Long> couponUsageReleaseScript() {
        return RedisScript.of(new ClassPathResource(COUPON_USAGE_RELEASE), Long.class);
    }
}
