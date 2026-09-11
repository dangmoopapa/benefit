package im.dangmoo.benefit.api.web.point.service;

import im.dangmoo.benefit.api.web.point.model.PointBookResponse;
import im.dangmoo.benefit.api.web.point.model.PointTransactionResponse;
import im.dangmoo.benefit.domain.data.point.balance.PointBalanceRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionRepository;
import im.dangmoo.benefit.domain.data.point.transaction.PointTransactionType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PointBookService {

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

    public List<PointTransactionResponse> getTransactions(
        final String userId,
        final List<PointTransactionType> types
    ) {
        return pointTransactionRepository.findByUserId(userId, types).stream()
            .map(PointTransactionResponse::of)
            .toList();
    }
}
