package im.dangmoo.benefit.domain.data.point.balance;

import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Document(collection = MongoCollections.POINT_BALANCES)
public class PointBalance {

    private static final String USER_ID = "userId";

    @Id
    private String id;
    private String userId;
    private long point;
    private List<PointLot> lots;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    @Version
    private Long version;

    private PointBalance() {
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static PointBalance create(final String userId, final String createdBy, final Instant now) {
        final PointBalance balance = new PointBalance();
        balance.userId = userId;
        balance.point = 0L;
        balance.lots = new ArrayList<>();
        balance.createdBy = createdBy;
        balance.createdAt = now;
        balance.updatedBy = createdBy;
        balance.updatedAt = now;
        return balance;
    }

    public long available(final Instant now) {
        return lots().stream()
            .filter(lot -> !lot.isExpired(now))
            .mapToLong(PointLot::getPoint)
            .sum();
    }

    public void issue(final long amount, final Instant expiresAt, final String updatedBy, final Instant now) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        purgeExpired(now);
        addLot(amount, expiresAt);
        this.point = sumLots();
        touch(updatedBy, now);
    }

    public void revoke(final long amount, final Instant expiresAt, final String updatedBy, final Instant now) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        purgeExpired(now);
        final PointLot lot = findLot(expiresAt);
        if (lot == null || lot.isExpired(now) || lot.getPoint() < amount) {
            throw new IllegalStateException("insufficient point");
        }
        lot.subtract(amount);
        lots().removeIf(item -> item.getPoint() <= 0);
        this.point = sumLots();
        touch(updatedBy, now);
    }

    public List<PointLot> spend(final long amount, final String updatedBy, final Instant now) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        purgeExpired(now);
        if (available(now) < amount) {
            throw new IllegalStateException("insufficient point");
        }
        final List<PointLot> used = new ArrayList<>();
        long remain = amount;
        for (final PointLot lot : lots()) {
            if (lot.isExpired(now) || remain <= 0) {
                continue;
            }
            final long consumed = Math.min(lot.getPoint(), remain);
            lot.subtract(consumed);
            used.add(PointLot.of(lot.getExpiresAt(), consumed));
            remain -= consumed;
        }
        lots().removeIf(lot -> lot.getPoint() <= 0);
        this.point = sumLots();
        touch(updatedBy, now);
        return List.copyOf(used);
    }

    public void restore(final List<PointLot> usedLots, final String updatedBy, final Instant now) {
        purgeExpired(now);
        for (final PointLot usedLot : usedLots) {
            if (usedLot.getPoint() <= 0) {
                continue;
            }
            addLot(usedLot.getPoint(), usedLot.getExpiresAt());
        }
        this.point = sumLots();
        touch(updatedBy, now);
    }

    private void touch(final String updatedBy, final Instant now) {
        this.updatedBy = updatedBy;
        this.updatedAt = now;
    }

    private void purgeExpired(final Instant now) {
        lots().removeIf(lot -> lot.isExpired(now));
    }

    private PointLot findLot(final Instant expiresAt) {
        for (final PointLot lot : lots()) {
            if (Objects.equals(lot.getExpiresAt(), expiresAt)) {
                return lot;
            }
        }
        return null;
    }

    private void addLot(final long amount, final Instant expiresAt) {
        for (final PointLot lot : lots()) {
            if (Objects.equals(lot.getExpiresAt(), expiresAt)) {
                lot.add(amount);
                return;
            }
        }
        lots().add(PointLot.of(expiresAt, amount));
        lots().sort(Comparator.comparing(PointLot::getExpiresAt, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private long sumLots() {
        return lots().stream().mapToLong(PointLot::getPoint).sum();
    }

    private List<PointLot> lots() {
        if (lots == null) {
            lots = new ArrayList<>();
        }
        return lots;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getPoint() {
        return point;
    }

    public List<PointLot> getLots() {
        return List.copyOf(lots());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
