package im.dangmoo.benefit.infrastructure.collection.cache;

import java.time.Duration;

public enum CacheDuration {

    COUPON_POLICY(CacheKeys.COUPON_POLICY, Duration.ofMinutes(30));

    private final String key;
    private final Duration duration;

    CacheDuration(final String key, final Duration duration) {
        this.key = key;
        this.duration = duration;
    }

    public String key() {
        return key;
    }

    public Duration duration() {
        return duration;
    }
}
