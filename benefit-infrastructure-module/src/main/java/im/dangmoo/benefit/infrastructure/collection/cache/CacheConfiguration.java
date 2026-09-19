package im.dangmoo.benefit.infrastructure.collection.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    RedisCacheConfiguration redisCacheConfiguration(final JsonMapper jsonMapper) {
        final JsonMapper cacheMapper = jsonMapper.rebuild()
            .changeDefaultVisibility(vc -> vc.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
            .build();

        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJacksonJsonRedisSerializer(cacheMapper)
                )
            );
    }

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
        final RedisCacheConfiguration redisCacheConfiguration
    ) {
        return builder -> {
            for (final CacheDuration cacheDuration : CacheDuration.values()) {
                builder.withCacheConfiguration(
                    cacheDuration.key(),
                    redisCacheConfiguration.entryTtl(cacheDuration.duration())
                );
            }
        };
    }
}
