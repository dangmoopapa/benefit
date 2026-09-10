package im.dangmoo.benefit.api.web.point.service;

import im.dangmoo.benefit.api.web.point.model.PointBookResponse;
import im.dangmoo.benefit.api.web.point.model.PointTransactionPageResponse;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.domain.data.point.balance.PointBalanceRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransaction;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import im.dangmoo.benefit.domain.infrastructure.mongo.DocumentSlice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PointBookService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final PointBalanceRepository pointBalanceRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public PointBookService(
        final PointBalanceRepository pointBalanceRepository,
        final PointTransactionRepository pointTransactionRepository
    ) {
        this.pointBalanceRepository = pointBalanceRepository;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public PointBookResponse getBalance(final String userId) {
        final Instant now = Instant.now();
        return pointBalanceRepository.findByUserId(userId)
            .map(balance -> PointBookResponse.of(balance, now))
            .orElseGet(() -> PointBookResponse.empty(userId));
    }

    public PointTransactionPageResponse getTransactions(
        final String userId,
        final List<PointTransactionType> types,
        final String cursor,
        final Integer size
    ) {
        final int resolvedSize = Math.clamp(size == null ? DEFAULT_SIZE : size, 1, MAX_SIZE);
        final DocumentSlice<PointTransaction> result = pointTransactionRepository.findByUserId(
            userId,
            types,
            cursor,
            resolvedSize
        );
        return PointTransactionPageResponse.of(
            result,
            result.content().stream().map(PointTransactionResponse::of).toList()
        );
    }
}
