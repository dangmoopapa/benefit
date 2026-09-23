package im.dangmoo.benefit.data.infrastructure;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class RedisRepository<T extends RedisKey> {

    protected final StringRedisTemplate redisTemplate;

    protected RedisRepository(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected String getValue(final T key) {
        return redisTemplate.opsForValue().get(key.getKey());
    }

    protected Map<String, String> multiGet(final List<T> keys, final Function<T, String> identifier) {
        final int size = keys.size();
        if (size == 0) {
            return Map.of();
        }

        final List<String> redisKeys = new ArrayList<>(size);
        for (T key : keys) {
            redisKeys.add(key.getKey());
        }

        final List<String> values = redisTemplate.opsForValue().multiGet(redisKeys);
        final Map<String, String> result = HashMap.newHashMap(size);
        for (int i = 0; i < size; i++) {
            result.put(
                identifier.apply(keys.get(i)),
                values == null ? null : values.get(i)
            );
        }
        return result;
    }

    protected long getAsLong(final T key) {
        return parseLong(getValue(key));
    }

    protected Map<String, Long> multiGetAsLong(final List<T> keys, final Function<T, String> identifier) {
        final Map<String, String> values = multiGet(keys, identifier);
        final Map<String, Long> result = HashMap.newHashMap(values.size());
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            result.put(entry.getKey(), parseLong(entry.getValue()));
        }
        return result;
    }

    protected long increment(final T key) {
        final Long value = redisTemplate.opsForValue().increment(key.getKey());
        return value == null ? 0L : value;
    }

    protected void decrement(final T key) {
        redisTemplate.opsForValue().decrement(key.getKey());
    }

    private long parseLong(final String value) {
        return value == null ? 0L : Long.parseLong(value);
    }
}
