package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.dto.point.PointTransactionListResponse;
import im.dangmoo.benefit.api.dto.point.PointTransactionPageRequest;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionType;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class PointTransactionSearchUseCase {

    private static final Set<PointTransactionType> VISIBLE = EnumSet.allOf(PointTransactionType.class);

    private final PointTransactionMongoRepository pointTransactionMongoRepository;

    public PointTransactionSearchUseCase(
        final PointTransactionMongoRepository pointTransactionMongoRepository
    ) {
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
    }

    public PointTransactionListResponse search(
        final String userId,
        final PointTransactionPageRequest request
    ) {
        final int page = request.pageOrDefault();
        final int size = request.sizeOrDefault();
        final long totalElements = pointTransactionMongoRepository.countByUserIdAndTypes(userId, VISIBLE);
        return PointTransactionListResponse.of(
            pointTransactionMongoRepository.findByUserIdAndTypes(userId, VISIBLE, page, size),
            totalElements,
            page,
            size
        );
    }
}
