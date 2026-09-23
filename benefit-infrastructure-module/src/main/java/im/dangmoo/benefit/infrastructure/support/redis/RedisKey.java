package im.dangmoo.benefit.infrastructure.support.redis;

public abstract class RedisKey {

    private final RedisKeyName redisKeyName;

    protected RedisKey(final RedisKeyName redisKeyName) {
        this.redisKeyName = redisKeyName;
    }

    public final String getKey() {
        return redisKeyName.key() + ":" + suffix();
    }

    protected abstract String suffix();
}
