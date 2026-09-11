package im.dangmoo.benefit.domain.infrastructure.cache;

import java.time.Duration;

public enum CacheType {
    MEMBERSHIP_POLICY("membership-policy", Duration.ofMinutes(10));

    private final String cacheName;
    private final Duration duration;

    CacheType(final String cacheName, final Duration duration) {
        this.cacheName = cacheName;
        this.duration = duration;
    }

    public String cacheName() {
        return cacheName;
    }

    public Duration duration() {
        return duration;
    }
}
