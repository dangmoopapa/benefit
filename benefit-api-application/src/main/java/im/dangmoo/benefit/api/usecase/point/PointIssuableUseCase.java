package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.model.point.PointIssuableRequest;
import im.dangmoo.benefit.api.model.point.PointIssuableResponse;
import im.dangmoo.benefit.api.usecase.ApiMessage;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.domain.point.PointTransactionDomain;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicy;
import im.dangmoo.benefit.infrastructure.data.point.policy.PointPolicyMongoRepository;
import im.dangmoo.benefit.infrastructure.data.point.stock.PointGrantStockRedisRepository;
import im.dangmoo.benefit.infrastructure.data.point.transaction.PointTransactionMongoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PointIssuableUseCase {

    private final PointPolicyMongoRepository pointPolicyMongoRepository;
    private final PointTransactionMongoRepository pointTransactionMongoRepository;
    private final PointGrantStockRedisRepository pointGrantStockRedisRepository;

    public PointIssuableUseCase(
        final PointPolicyMongoRepository pointPolicyMongoRepository,
        final PointTransactionMongoRepository pointTransactionMongoRepository,
        final PointGrantStockRedisRepository pointGrantStockRedisRepository
    ) {
        this.pointPolicyMongoRepository = pointPolicyMongoRepository;
        this.pointTransactionMongoRepository = pointTransactionMongoRepository;
        this.pointGrantStockRedisRepository = pointGrantStockRedisRepository;
    }

    public PointIssuableResponse issuable(final String userId, final PointIssuableRequest request) {
        final PointPolicy policy = pointPolicyMongoRepository.findByKey(request.policyKey()).orElse(null);
        if (policy == null || policy.getStatus().isNotActive()) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_POINT);
        }

        if (!PointIssueDomain.of(policy.getIssueCondition()).isSatisfiedAt(Instant.now())) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_POINT);
        }

        if (pointTransactionMongoRepository.findByIdempotencyKey(
            PointTransactionDomain.grantKey(policy.getId(), userId)
        ).isPresent()) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.ALREADY_GRANTED_POINT);
        }

        final Long stockQuantity = policy.getIssueCondition().getStockQuantity();
        if (stockQuantity != null && pointGrantStockRedisRepository.get(policy.getId()) >= stockQuantity) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.STOCK_EXHAUSTED_POINT);
        }

        return PointIssuableResponse.ofIssuable();
    }
}
