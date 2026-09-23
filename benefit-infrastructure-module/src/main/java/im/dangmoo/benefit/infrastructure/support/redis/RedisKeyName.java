package im.dangmoo.benefit.infrastructure.support.redis;

public enum RedisKeyName {

    COUPON_ISSUE_STOCK("COUPON:ISSUE:STOCK"),
    COUPON_ISSUE_MARKER("COUPON:ISSUE:MARKER"),
    COUPON_USAGE_STOCK("COUPON:USAGE:STOCK"),
    POINT_GRANT_STOCK("POINT:GRANT:STOCK");

    private final String key;

    RedisKeyName(final String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
