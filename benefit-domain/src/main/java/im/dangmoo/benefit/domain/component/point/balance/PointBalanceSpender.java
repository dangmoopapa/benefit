package im.dangmoo.benefit.domain.component.point.balance;

import im.dangmoo.benefit.domain.data.point.balance.PointBalance;
import im.dangmoo.benefit.domain.data.point.balance.PointLot;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class PointBalanceSpender {

    private final PointBalanceWriter pointBalanceWriter;

    public PointBalanceSpender(final PointBalanceWriter pointBalanceWriter) {
        this.pointBalanceWriter = pointBalanceWriter;
    }

    public List<PointLot> spend(
        final String userId,
        final long amount,
        final String actorId,
        final Instant now
    ) {
        return pointBalanceWriter.updateAndGet(
            userId,
            actorId,
            now,
            balance -> balance.spend(amount, actorId, now)
        );
    }

    public PointBalance restore(
        final String userId,
        final List<PointLot> usedLots,
        final String actorId,
        final Instant now
    ) {
        return pointBalanceWriter.update(
            userId,
            actorId,
            now,
            balance -> balance.restore(usedLots, actorId, now)
        );
    }
}
