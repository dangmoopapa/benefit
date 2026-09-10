package im.dangmoo.benefit.admin.web.point.service;

import im.dangmoo.benefit.admin.web.point.model.PointBalanceResponse;
import im.dangmoo.benefit.domain.data.point.balance.PointBalanceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PointBalanceService {

    private final PointBalanceRepository pointBalanceRepository;

    public PointBalanceService(final PointBalanceRepository pointBalanceRepository) {
        this.pointBalanceRepository = pointBalanceRepository;
    }

    public PointBalanceResponse get(final String userId) {
        final Instant now = Instant.now();
        return pointBalanceRepository.findByUserId(userId)
            .map(balance -> PointBalanceResponse.of(balance, now))
            .orElseGet(() -> PointBalanceResponse.empty(userId));
    }
}
