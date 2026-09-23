package im.dangmoo.benefit.api.usecase.point;

import im.dangmoo.benefit.api.dto.point.PointIssuableRequest;
import im.dangmoo.benefit.api.dto.point.PointIssuableResponse;
import im.dangmoo.benefit.api.usecase.ApiMessage;
import im.dangmoo.benefit.domain.point.PointExpireDomain;
import im.dangmoo.benefit.domain.point.PointIssueDomain;
import im.dangmoo.benefit.data.entity.point.policy.PointPolicyDocument;
import im.dangmoo.benefit.data.infrastructure.point.PointPolicyMongoRepository;
import im.dangmoo.benefit.data.infrastructure.point.PointGrantStockRedisRepository;
import im.dangmoo.benefit.data.infrastructure.point.PointTransactionMongoRepository;
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
        final PointPolicyDocument policy = pointPolicyMongoRepository.findByKey(request.policyKey()).orElse(null);
        if (policy == null) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_POINT);
        }

        final Instant now = Instant.now();
        final PointIssueDomain pointIssue = PointIssueDomain.of(policy);
        final String grantKey = pointIssue.grantKeyFor(policy.getId(), userId, now);
        final boolean alreadyGranted = pointTransactionMongoRepository.findByIdempotencyKey(grantKey).isPresent();
        if (PointExpireDomain.of(policy, now).isExpiredAt(now)) {
            return PointIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_POINT);
        }

        final long grantedCount = pointGrantStockRedisRepository.get(policy.getId());
        return switch (pointIssue.issuabilityAt(now, alreadyGranted, grantedCount)) {
            case ALREADY_GRANTED -> PointIssuableResponse.ofNotIssuable(ApiMessage.ALREADY_GRANTED_POINT);
            case STOCK_EXHAUSTED -> PointIssuableResponse.ofNotIssuable(ApiMessage.STOCK_EXHAUSTED_POINT);
            case POLICY_INACTIVE, OUT_OF_PERIOD ->
                PointIssuableResponse.ofNotIssuable(ApiMessage.POLICY_ISSUE_POINT);
            case ISSUABLE -> PointIssuableResponse.ofIssuable();
        };
    }
}
