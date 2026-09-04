package im.dangmoo.benefit.admin.web.coupon.service;

import im.dangmoo.benefit.admin.support.ApiException;
import im.dangmoo.benefit.admin.support.ApiMessage;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyRequest;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicyResponse;
import im.dangmoo.benefit.admin.web.coupon.model.CouponPolicySearchRequest;
import im.dangmoo.benefit.domain.coupon.policy.CouponPolicy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponPolicyAdminService {

    private final MongoTemplate mongoTemplate;

    public CouponPolicyAdminService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CouponPolicyResponse> list(final CouponPolicySearchRequest request) {
        final Query query = CouponPolicy.query(
            request.code(),
            request.name(),
            request.platformId(),
            request.type(),
            request.status()
        );
        return mongoTemplate.find(query, CouponPolicy.class).stream()
            .map(CouponPolicyResponse::of)
            .toList();
    }

    public CouponPolicyResponse get(final String policyId) {
        final CouponPolicy policy = Optional.ofNullable(mongoTemplate.findById(policyId, CouponPolicy.class))
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        return CouponPolicyResponse.of(policy);
    }

    public CouponPolicyResponse create(final CouponPolicyRequest request) {
        final Query query = CouponPolicy.queryByCode(request.code());
        if (mongoTemplate.exists(query, CouponPolicy.class)) {
            throw new ApiException(ApiMessage.DUPLICATE_CODE);
        }
        return CouponPolicyResponse.of(mongoTemplate.save(request.toDocument()));
    }

    public CouponPolicyResponse update(final String policyId, final CouponPolicyRequest request) {
        final CouponPolicy policy = Optional.ofNullable(mongoTemplate.findById(policyId, CouponPolicy.class))
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        if (policy.isActive()) {
            policy.updateIssueCondition(request.issueCondition().toDocument());
            return CouponPolicyResponse.of(mongoTemplate.save(policy));
        }

        policy.update(
            request.code(),
            request.name(),
            request.description(),
            request.platformId(),
            request.type(),
            request.status(),
            request.issueCondition().toDocument(),
            request.benefitCondition().toDocument(),
            request.applyCondition().toDocument(),
            request.usageCondition().toDocument(),
            request.lifecycleCondition().toDocument()
        );
        return CouponPolicyResponse.of(mongoTemplate.save(policy));
    }

    public CouponPolicyResponse activate(final String policyId) {
        final CouponPolicy policy = Optional.ofNullable(mongoTemplate.findById(policyId, CouponPolicy.class))
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        policy.activate();
        return CouponPolicyResponse.of(mongoTemplate.save(policy));
    }

    public CouponPolicyResponse suspend(final String policyId) {
        final CouponPolicy policy = Optional.ofNullable(mongoTemplate.findById(policyId, CouponPolicy.class))
            .orElseThrow(() -> new ApiException(ApiMessage.NOT_FOUND));
        policy.suspend();
        return CouponPolicyResponse.of(mongoTemplate.save(policy));
    }
}
