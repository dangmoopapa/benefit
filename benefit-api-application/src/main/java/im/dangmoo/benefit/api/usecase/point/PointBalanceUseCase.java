package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointBalanceResponse;
import im.dangmoo.benefit.infrastructure.data.point.balance.PointBalanceMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PointBalanceUseCase {

    private final PointBalanceMongoRepository pointBalanceMongoRepository;

    public PointBalanceUseCase(final PointBalanceMongoRepository pointBalanceMongoRepository) {
        this.pointBalanceMongoRepository = pointBalanceMongoRepository;
    }

    public PointBalanceResponse execute(final String userId) {
        return pointBalanceMongoRepository.findByUserId(userId)
            .map(balance -> PointBalanceResponse.of(balance, Instant.now()))
            .orElseGet(PointBalanceResponse::empty);
    }
}
