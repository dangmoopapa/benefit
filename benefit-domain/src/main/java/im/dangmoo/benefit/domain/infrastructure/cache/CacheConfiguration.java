package im.dangmoo.benefit.domain.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    CacheManager cacheManager() {
        final SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
            Arrays.stream(CacheType.values())
                .map(type -> new CaffeineCache(
                    type.cacheName(),
                    Caffeine.newBuilder()
                        .expireAfterWrite(type.duration())
                        .maximumSize(1_000)
                        .build()
                ))
                .toList()
        );
        return cacheManager;
    }
}
