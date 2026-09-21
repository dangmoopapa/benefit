package im.dangmoo.benefit.api.model.point;

import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalance;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PointBalanceResponse(
    long totalAmount,
    long expiringAmount,
    Instant syncedAt,
    List<Bucket> amountsByExpiresAt
) {

    public static PointBalanceResponse of(final PointBalance balance, final Instant now) {
        final PointBalanceDomain domain = PointBalanceDomain.of(balance);
        final List<Bucket> buckets = domain.availableAmountsByExpiresAt(now).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> Bucket.of(entry.getKey(), entry.getValue()))
            .toList();
        return new PointBalanceResponse(
            domain.totalAmount(),
            domain.expiringAmount(now),
            balance.getSyncedAt(),
            buckets
        );
    }

    public static PointBalanceResponse empty() {
        return new PointBalanceResponse(0L, 0L, null, List.of());
    }

    public record Bucket(
        Instant expiresAt,
        boolean neverExpires,
        long amount
    ) {

        public static Bucket of(final Instant expiresAt, final long amount) {
            return new Bucket(
                PointExpireDomain.toClientExpiresAt(expiresAt),
                PointExpireDomain.isNever(expiresAt),
                amount
            );
        }
    }
}
