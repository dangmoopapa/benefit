package im.dangmoo.benefit.domain.component.point.balance;

import im.dangmoo.benefit.domain.data.point.balance.PointBalance;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PointBalanceIssuer {

    private final PointBalanceWriter pointBalanceWriter;

    public PointBalanceIssuer(final PointBalanceWriter pointBalanceWriter) {
        this.pointBalanceWriter = pointBalanceWriter;
    }

    public PointBalance issue(
        final String userId,
        final long amount,
        final Instant expiresAt,
        final String actorId,
        final Instant now
    ) {
        return pointBalanceWriter.update(
            userId,
            actorId,
            now,
            balance -> balance.issue(amount, expiresAt, actorId, now)
        );
    }

    public PointBalance revoke(
        final String userId,
        final long amount,
        final Instant expiresAt,
        final String actorId,
        final Instant now
    ) {
        return pointBalanceWriter.update(
            userId,
            actorId,
            now,
            balance -> balance.revoke(amount, expiresAt, actorId, now)
        );
    }
}
