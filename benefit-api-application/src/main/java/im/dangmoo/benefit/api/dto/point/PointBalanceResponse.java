package im.dangmoo.benefit.api.dto.point;

import im.dangmoo.benefit.domain.point.PointBalanceDomain;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.data.entity.point.balance.PointBalanceDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PointBalanceResponse(
    long totalAmount,
    long expiringAmount,
    Instant syncedAt,
    List<Bucket> amountsByExpiresAt
) {

    public static PointBalanceResponse of(final PointBalanceDocument balance, final Instant now) {
        final PointBalanceDomain pointBalance = PointBalanceDomain.of(balance);
        final List<Bucket> buckets = pointBalance.availableAmountsByExpiresAt(now).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(bucket -> Bucket.of(bucket.getKey(), bucket.getValue()))
            .toList();
        return new PointBalanceResponse(
            pointBalance.totalAmount(),
            pointBalance.expiredAmountAt(now),
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
            final PointExpireDomain pointExpire = PointExpireDomain.of(expiresAt);
            return new Bucket(
                pointExpire.expiresAtOrNull(),
                pointExpire.neverExpires(),
                amount
            );
        }
    }
}
