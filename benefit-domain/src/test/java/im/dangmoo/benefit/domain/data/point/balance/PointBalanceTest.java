package im.dangmoo.benefit.domain.data.point.balance;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointBalanceTest {

    private static final Instant NOW = Instant.parse("2026-09-11T00:00:00Z");
    private static final Instant LATER = Instant.parse("2026-09-20T00:00:00Z");
    private static final Instant SOON = Instant.parse("2026-09-12T00:00:00Z");

    @Test
    void mergesSameExpiresAt() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(100, LATER, "test", NOW);
        balance.issue(50, LATER, "test", NOW);

        assertEquals(1, balance.getLots().size());
        assertEquals(150, balance.getLots().getFirst().getPoint());
        assertEquals(150, balance.getPoint());
        assertEquals(150, balance.available(NOW));
    }

    @Test
    void readDoesNotPurgeExpiredLots() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(100, SOON, "test", NOW);
        balance.issue(40, LATER, "test", NOW);

        assertEquals(140, balance.getPoint());
        assertEquals(2, balance.getLots().size());
        assertEquals(40, balance.available(SOON));
        assertEquals(2, balance.getLots().size());
        assertEquals(140, balance.getPoint());
    }

    @Test
    void writePurgesExpiredLots() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(100, SOON, "test", NOW);
        balance.issue(40, LATER, "test", NOW);

        balance.issue(10, LATER, "test", SOON);

        assertEquals(1, balance.getLots().size());
        assertEquals(LATER, balance.getLots().getFirst().getExpiresAt());
        assertEquals(50, balance.getPoint());
        assertEquals(50, balance.available(SOON));
    }

    @Test
    void spendUsesEarliestExpiresAtFirst() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(30, LATER, "test", NOW);
        balance.issue(20, SOON, "test", NOW);

        final List<PointLot> used = balance.spend(25, "test", NOW);

        assertEquals(2, used.size());
        assertEquals(SOON, used.getFirst().getExpiresAt());
        assertEquals(20, used.getFirst().getPoint());
        assertEquals(LATER, used.getLast().getExpiresAt());
        assertEquals(5, used.getLast().getPoint());
        assertEquals(1, balance.getLots().size());
        assertEquals(25, balance.getPoint());
    }

    @Test
    void revokeSubtractsMatchingLot() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(30, LATER, "test", NOW);
        balance.issue(20, SOON, "test", NOW);

        balance.revoke(10, LATER, "test", NOW);

        assertEquals(40, balance.getPoint());
        assertEquals(20, balance.getLots().stream().filter(l -> SOON.equals(l.getExpiresAt())).findFirst().orElseThrow().getPoint());
        assertEquals(20, balance.getLots().stream().filter(l -> LATER.equals(l.getExpiresAt())).findFirst().orElseThrow().getPoint());
    }

    @Test
    void restorePutsUsedLotsBack() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(30, LATER, "test", NOW);
        final List<PointLot> used = balance.spend(10, "test", NOW);

        balance.restore(used, "test", NOW);

        assertEquals(30, balance.getPoint());
        assertEquals(1, balance.getLots().size());
        assertEquals(30, balance.getLots().getFirst().getPoint());
    }

    @Test
    void spendRejectsWhenInsufficient() {
        final PointBalance balance = PointBalance.create("u1", "test", NOW);
        balance.issue(10, LATER, "test", NOW);

        assertThrows(IllegalStateException.class, () -> balance.spend(11, "test", NOW));
    }
}
