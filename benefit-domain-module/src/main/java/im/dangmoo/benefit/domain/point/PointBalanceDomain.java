package im.dangmoo.benefit.domain.point;

import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class PointBalanceDomain {

    private final long totalAmount;
    private final Map<Instant, Long> amountsByExpiresAt;

    private PointBalanceDomain(final long totalAmount, final Map<Instant, Long> amountsByExpiresAt) {
        this.totalAmount = totalAmount;
        this.amountsByExpiresAt = amountsByExpiresAt;
    }

    public static PointBalanceDomain of(final PointBalance balance) {
        return new PointBalanceDomain(balance.getTotalAmount(), balance.getAmountsByExpiresAt());
    }

    public static PointBalanceDomain of(final Map<Instant, Long> amountsByExpiresAt) {
        long total = 0L;
        for (final long amount : amountsByExpiresAt.values()) {
            total += amount;
        }
        return new PointBalanceDomain(total, amountsByExpiresAt);
    }

    public long totalAmount() {
        return totalAmount;
    }

    public long expiringAmount(final Instant now) {
        return Math.max(0L, totalAmount - availableAmount(now));
    }

    public long availableAmount(final Instant now) {
        long total = 0L;
        for (final Map.Entry<Instant, Long> entry : amountsByExpiresAt.entrySet()) {
            if (isAvailable(entry.getKey(), now)) {
                total += entry.getValue();
            }
        }
        return total;
    }

    public Map<Instant, Long> availableAmountsByExpiresAt(final Instant now) {
        final Map<Instant, Long> active = new LinkedHashMap<>();
        for (final Map.Entry<Instant, Long> entry : amountsByExpiresAt.entrySet()) {
            if (isAvailable(entry.getKey(), now) && entry.getValue() > 0) {
                active.put(entry.getKey(), entry.getValue());
            }
        }
        return active;
    }

    private boolean isAvailable(final Instant expiresAt, final Instant now) {
        return expiresAt.isAfter(now);
    }
}
