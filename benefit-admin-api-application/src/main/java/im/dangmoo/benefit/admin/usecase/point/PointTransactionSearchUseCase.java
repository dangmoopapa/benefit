package im.dangmoo.benefit.admin.usecase.point;

import im.dangmoo.benefit.admin.dto.point.transaction.PointTransactionSearchRequest;
import im.dangmoo.benefit.admin.dto.point.transaction.PointTransactionSearchResponse;
import im.dangmoo.benefit.admin.usecase.ApiException;
import im.dangmoo.benefit.data.entity.point.transaction.PointTransactionDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PointTransactionSearchUseCase {

    private final PointTransactionMongoRepository pointTransactionMongoRepository;

    public PointTransactionSearchUseCase(
        final PointTransactionMongoRepository pointTransactionMongoRepository
    ) {
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
    }

    public PointTransactionSearchResponse search(final PointTransactionSearchRequest request) {
        final String userId = request.userId();
        final String policyId = request.policyId();
        if (!StringUtils.hasText(userId) && !StringUtils.hasText(policyId)) {
            throw ApiException.conditionNotSatisfied();
        }
        final int page = request.pageOrDefault();
        final int size = request.sizeOrDefault();
        final long totalElements = pointTransactionMongoRepository.count(userId, policyId);
        final List<PointTransactionDocument> transactions = pointTransactionMongoRepository.search(
            userId,
            policyId,
            page,
            size
        );
        return PointTransactionSearchResponse.of(transactions, totalElements, page, size);
    }
}
