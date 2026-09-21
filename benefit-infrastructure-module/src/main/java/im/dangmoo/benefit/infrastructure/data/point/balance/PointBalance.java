package im.dangmoo.benefit.infrastructure.data.point.balance;

import im.dangmoo.benefit.infrastructure.collection.mongo.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Sort;

@Document(collection = MongoDocuments.POINT_BALANCES)
public class PointBalance {

    public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    public static final Instant NEVER_EXPIRES_AT = Instant.parse("9999-12-31T00:00:00Z");
    public static final String USER_ID = "userId";
    public static final String NEXT_EXPIRES_AT = "nextExpiresAt";

    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private long totalAmount;
    private Map<Instant, Long> amountsByExpiresAt = new LinkedHashMap<>();
    private Instant nextExpiresAt;
    private Instant syncedAt;
    private Instant updatedAt;
    @Version
    private Long version;

    private PointBalance() {
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static Query queryDueForExpire(final Instant asOf) {
        return Query.query(Criteria.where(NEXT_EXPIRES_AT).lte(asOf))
            .with(Sort.by(Sort.Direction.ASC, NEXT_EXPIRES_AT));
    }

    public static boolean isNever(final Instant expiresAt) {
        return expiresAt == null || NEVER_EXPIRES_AT.equals(expiresAt);
    }

    public static Instant toExpiresKey(final Instant expiresAt) {
        if (isNever(expiresAt)) {
            return NEVER_EXPIRES_AT;
        }
        final LocalDate day = expiresAt.atZone(ZONE).toLocalDate();
        return day.atStartOfDay(ZONE).toInstant();
    }

    public static PointBalance create(final String userId) {
        final PointBalance balance = new PointBalance();
        balance.userId = Objects.requireNonNull(userId);
        balance.totalAmount = 0L;
        balance.amountsByExpiresAt = new LinkedHashMap<>();
        balance.updatedAt = Instant.now();
        return balance;
    }

    public PointBalance increase(final Instant expiresAt, final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        final Instant key = toExpiresKey(expiresAt);
        amountsByExpiresAt.merge(key, amount, Long::sum);
        totalAmount += amount;
        refreshNextExpiresAt();
        updatedAt = Instant.now();
        return this;
    }

    public PointBalance decrease(final Instant expiresAt, final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        final Instant key = toExpiresKey(expiresAt);
        final long current = amountsByExpiresAt.getOrDefault(key, 0L);
        if (current < amount) {
            throw new IllegalStateException("insufficient balance bucket");
        }
        final long next = current - amount;
        if (next == 0) {
            amountsByExpiresAt.remove(key);
        } else {
            amountsByExpiresAt.put(key, next);
        }
        totalAmount -= amount;
        refreshNextExpiresAt();
        updatedAt = Instant.now();
        return this;
    }

    public void decreaseByExpiresAt(final long amount, final Instant now) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        long left = amount;
        final List<Map.Entry<Instant, Long>> buckets = new ArrayList<>(amountsByExpiresAt.entrySet());
        buckets.sort(Map.Entry.comparingByKey());
        for (final Map.Entry<Instant, Long> bucket : buckets) {
            if (left <= 0) {
                break;
            }
            if (bucket.getValue() <= 0 || !bucket.getKey().isAfter(now)) {
                continue;
            }
            final long take = Math.min(bucket.getValue(), left);
            decrease(bucket.getKey(), take);
            left -= take;
        }
        if (left > 0) {
            throw new IllegalStateException("insufficient point");
        }
    }

    public PointBalance syncExpired(final Instant syncedAt) {
        long removed = 0L;
        final Iterator<Map.Entry<Instant, Long>> it = amountsByExpiresAt.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Instant, Long> entry = it.next();
            if (isNever(entry.getKey())) {
                continue;
            }
            if (!entry.getKey().isAfter(syncedAt)) {
                removed += entry.getValue();
                it.remove();
            }
        }
        totalAmount -= removed;
        this.syncedAt = syncedAt;
        refreshNextExpiresAt();
        updatedAt = Instant.now();
        return this;
    }

    private void refreshNextExpiresAt() {
        Instant next = null;
        for (final Instant key : amountsByExpiresAt.keySet()) {
            if (isNever(key)) {
                continue;
            }
            if (next == null || key.isBefore(next)) {
                next = key;
            }
        }
        this.nextExpiresAt = next;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public Map<Instant, Long> getAmountsByExpiresAt() {
        return amountsByExpiresAt;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
