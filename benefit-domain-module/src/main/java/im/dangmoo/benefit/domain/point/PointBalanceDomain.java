package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PointBalanceDomain {

    private final long totalAmount;
    private final Map<Instant, Long> amountsByExpiresAt;

    private PointBalanceDomain(final long totalAmount, final Map<Instant, Long> amountsByExpiresAt) {
        this.totalAmount = totalAmount;
        this.amountsByExpiresAt = amountsByExpiresAt;
    }

    public static PointBalanceDomain of(final PointBalanceDocument balance) {
        return new PointBalanceDomain(balance.getTotalAmount(), balance.getAmountsByExpiresAt());
    }

    public long totalAmount() {
        return totalAmount;
    }

    public long availableAmountAt(final Instant now) {
        return sumOf(availableAmountsByExpiresAt(now));
    }

    public long expiredAmountAt(final Instant now) {
        return sumOf(expiredAmountsByExpiresAt(now));
    }

    public Map<Instant, Long> availableAmountsByExpiresAt(final Instant now) {
        return bucketsAliveAt(now, true);
    }

    public Map<Instant, Long> expiredAmountsByExpiresAt(final Instant now) {
        return bucketsAliveAt(now, false);
    }

    private Map<Instant, Long> bucketsAliveAt(final Instant now, final boolean alive) {
        final Map<Instant, Long> picked = new LinkedHashMap<>();
        for (final Map.Entry<Instant, Long> bucket : amountsByExpiresAt.entrySet()) {
            if (isAliveAt(bucket.getKey(), now) == alive && bucket.getValue() > 0) {
                picked.put(bucket.getKey(), bucket.getValue());
            }
        }
        return picked;
    }

    private boolean isAliveAt(final Instant expiresAt, final Instant now) {
        return expiresAt.isAfter(now);
    }

    private static long sumOf(final Map<Instant, Long> buckets) {
        long total = 0L;
        for (final Long amount : buckets.values()) {
            total += amount;
        }
        return total;
    }
}
