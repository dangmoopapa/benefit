package im.dangmoo.benefit.domain.data.point.balance;

import java.time.Instant;

public class PointLot {

    private Instant expiresAt;
    private long point;

    private PointLot() {
    }

    public static PointLot of(final Instant expiresAt, final long point) {
        final PointLot lot = new PointLot();
        lot.expiresAt = expiresAt;
        lot.point = point;
        return lot;
    }

    void add(final long amount) {
        point += amount;
    }

    void subtract(final long amount) {
        point -= amount;
    }

    boolean isExpired(final Instant now) {
        return expiresAt != null && !expiresAt.isAfter(now);
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public long getPoint() {
        return point;
    }
}
