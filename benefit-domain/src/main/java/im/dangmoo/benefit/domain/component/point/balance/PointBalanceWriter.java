package im.dangmoo.benefit.domain.component.point.balance;

import im.dangmoo.benefit.domain.data.point.balance.PointBalance;
import im.dangmoo.benefit.domain.data.point.balance.PointBalanceRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.Function;

@Component
public class PointBalanceWriter {

    private static final int MAX_RETRY = 3;

    private final PointBalanceRepository pointBalanceRepository;

    public PointBalanceWriter(final PointBalanceRepository pointBalanceRepository) {
        this.pointBalanceRepository = pointBalanceRepository;
    }

    public PointBalance update(
        final String userId,
        final String actorId,
        final Instant now,
        final Function<PointBalance, PointBalance> mutation
    ) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = pointBalanceRepository.getOrCreate(userId, actorId, now);
            try {
                return pointBalanceRepository.save(mutation.apply(balance));
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }

    public <T> T updateAndGet(
        final String userId,
        final String actorId,
        final Instant now,
        final Function<PointBalance, T> mutation
    ) {
        OptimisticLockingFailureException last = null;
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            final PointBalance balance = pointBalanceRepository.getOrCreate(userId, actorId, now);
            final T result = mutation.apply(balance);
            try {
                pointBalanceRepository.save(balance);
                return result;
            } catch (final OptimisticLockingFailureException ex) {
                last = ex;
            }
        }
        throw last;
    }
}
