package im.dangmoo.benefit.domain.data.coupon.policy;

import im.dangmoo.benefit.domain.data.coupon.policy.apply.CouponApplyCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.benefit.CouponBenefitCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.issue.CouponIssueCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.lifecycle.CouponLifecycleCondition;
import im.dangmoo.benefit.domain.data.coupon.policy.usage.CouponUsageCondition;
import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Document(collection = MongoCollections.COUPON_POLICIES)
public class CouponPolicy {

    @Id
    private String id;
    private String code;
    private String name;
    private String description;
    private String platformId;
    private CouponPolicyType type;
    private CouponPolicyStatus status;
    private CouponIssueCondition issueCondition;
    private CouponBenefitCondition benefitCondition;
    private CouponApplyCondition applyCondition;
    private CouponUsageCondition usageCondition;
    private CouponLifecycleCondition lifecycleCondition;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String PLATFORM_ID = "platformId";
    private static final String TYPE = "type";
    private static final String STATUS = "status";

    private CouponPolicy() {
    }

    public static Query query(
        final String code,
        final String name,
        final String platformId,
        final CouponPolicyType type,
        final CouponPolicyStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(code)) {
            query.addCriteria(Criteria.where(CODE).is(code));
        }
        if (StringUtils.hasText(name)) {
            final CouponPolicy probe = new CouponPolicy();
            probe.name = name;
            final ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher(NAME, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
            query.addCriteria(new Criteria().alike(Example.of(probe, matcher)));
        }
        if (StringUtils.hasText(platformId)) {
            query.addCriteria(Criteria.where(PLATFORM_ID).is(platformId));
        }
        if (type != null) {
            query.addCriteria(Criteria.where(TYPE).is(type));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByCode(final String code) {
        return Query.query(Criteria.where(CODE).is(code));
    }

    public static CouponPolicy create(
        final String code,
        final String name,
        final String description,
        final String platformId,
        final CouponPolicyType type,
        final CouponIssueCondition issueCondition,
        final CouponBenefitCondition benefitCondition,
        final CouponApplyCondition applyCondition,
        final CouponUsageCondition usageCondition,
        final CouponLifecycleCondition lifecycleCondition,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final CouponPolicy document = new CouponPolicy();
        document.code = code;
        document.name = name;
        document.description = description;
        document.platformId = platformId;
        document.type = type;
        document.status = CouponPolicyStatus.DRAFT;
        document.issueCondition = issueCondition;
        document.benefitCondition = benefitCondition;
        document.applyCondition = applyCondition;
        document.usageCondition = usageCondition;
        document.lifecycleCondition = lifecycleCondition;
        document.createdBy = createdBy;
        document.createdAt = now;
        document.updatedBy = createdBy;
        document.updatedAt = now;
        return document;
    }

    public void update(
        final String code,
        final String name,
        final String description,
        final String platformId,
        final CouponPolicyType type,
        final CouponIssueCondition issueCondition,
        final CouponBenefitCondition benefitCondition,
        final CouponApplyCondition applyCondition,
        final CouponUsageCondition usageCondition,
        final CouponLifecycleCondition lifecycleCondition,
        final String updatedBy
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.platformId = platformId;
        this.type = type;
        this.issueCondition = issueCondition;
        this.benefitCondition = benefitCondition;
        this.applyCondition = applyCondition;
        this.usageCondition = usageCondition;
        this.lifecycleCondition = lifecycleCondition;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void updateIssueCondition(final CouponIssueCondition issueCondition, final String updatedBy) {
        this.issueCondition = issueCondition;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void activate(final String updatedBy) {
        this.status = CouponPolicyStatus.ACTIVE;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void suspend(final String updatedBy) {
        this.status = CouponPolicyStatus.SUSPENDED;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == CouponPolicyStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPlatformId() {
        return platformId;
    }

    public CouponPolicyType getType() {
        return type;
    }

    public CouponPolicyStatus getStatus() {
        return status;
    }

    public CouponIssueCondition getIssueCondition() {
        return issueCondition;
    }

    public CouponBenefitCondition getBenefitCondition() {
        return benefitCondition;
    }

    public CouponApplyCondition getApplyCondition() {
        return applyCondition;
    }

    public CouponUsageCondition getUsageCondition() {
        return usageCondition;
    }

    public CouponLifecycleCondition getLifecycleCondition() {
        return lifecycleCondition;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
