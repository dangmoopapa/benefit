package im.dangmoo.benefit.data.entity.coupon.policy;

import im.dangmoo.benefit.data.entity.coupon.policy.condition.*;
import im.dangmoo.benefit.data.infrastructure.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = MongoDocuments.COUPON_POLICIES)
public class CouponPolicyDocument {

    @Id
    private String id;
    private String name;
    private String description;
    @Indexed(unique = true)
    private String key;
    private CouponPolicyType type;
    private CouponPolicyStatus status;
    private CouponBenefitCondition benefitCondition;
    private CouponIssueCondition issueCondition;
    private CouponUsageCondition usageCondition;
    private CouponApplyCondition applyCondition;
    private CouponLifecycleCondition lifecycleCondition;
    private CouponAccountCondition accountCondition;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    private CouponPolicyDocument() {
    }

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String STATUS = "status";
    public static final String APPLY_PRODUCT_IDS = "applyCondition.productIds";
    public static final String APPLY_BRAND_IDS = "applyCondition.brandIds";

    public static Query query(
        final String key,
        final String name,
        final CouponPolicyType type,
        final CouponPolicyStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(key)) {
            query.addCriteria(Criteria.where(KEY).is(key));
        }
        if (StringUtils.hasText(name)) {
            query.addCriteria(Criteria.where(NAME).regex(name, "i"));
        }
        if (type != null) {
            query.addCriteria(Criteria.where(TYPE).is(type));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByKey(final String key) {
        return Query.query(Criteria.where(KEY).is(key));
    }

    public static Query queryActiveVouchers(final String productId, final String brandId) {
        final Criteria typeAndStatus = new Criteria().andOperator(
            Criteria.where(TYPE).is(CouponPolicyType.VOUCHER),
            Criteria.where(STATUS).is(CouponPolicyStatus.ACTIVE)
        );

        final List<Criteria> targets = new ArrayList<>();
        if (StringUtils.hasText(productId)) {
            targets.add(Criteria.where(APPLY_PRODUCT_IDS).is(productId));
        }
        if (StringUtils.hasText(brandId)) {
            targets.add(Criteria.where(APPLY_BRAND_IDS).is(brandId));
        }
        if (targets.isEmpty()) {
            return Query.query(Criteria.where("_id").is(null));
        }

        return Query.query(new Criteria().andOperator(
            typeAndStatus,
            new Criteria().orOperator(targets.toArray(Criteria[]::new))
        ));
    }

    public static Query queryVouchers(
        final String productId,
        final String brandId,
        final CouponPolicyStatus status
    ) {
        final Query query = new Query();
        query.addCriteria(Criteria.where(TYPE).is(CouponPolicyType.VOUCHER));
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        if (StringUtils.hasText(productId)) {
            query.addCriteria(Criteria.where(APPLY_PRODUCT_IDS).is(productId));
        }
        if (StringUtils.hasText(brandId)) {
            query.addCriteria(Criteria.where(APPLY_BRAND_IDS).is(brandId));
        }
        return query;
    }

    public static CouponPolicyDocument create(
        final String name,
        final String description,
        final String key,
        final CouponPolicyType type,
        final CouponBenefitCondition benefitCondition,
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition,
        final CouponApplyCondition applyCondition,
        final CouponLifecycleCondition lifecycleCondition,
        final CouponAccountCondition accountCondition,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final CouponPolicyDocument policy = new CouponPolicyDocument();
        policy.name = name;
        policy.description = description;
        policy.key = key;
        policy.type = type;
        policy.status = CouponPolicyStatus.DRAFT;
        policy.benefitCondition = Objects.requireNonNull(benefitCondition);
        policy.issueCondition = Objects.requireNonNull(issueCondition);
        policy.usageCondition = Objects.requireNonNull(usageCondition);
        policy.applyCondition = Objects.requireNonNull(applyCondition);
        policy.lifecycleCondition = Objects.requireNonNull(lifecycleCondition);
        policy.accountCondition = Objects.requireNonNull(accountCondition);
        policy.createdBy = createdBy;
        policy.createdAt = now;
        policy.updatedBy = createdBy;
        policy.updatedAt = now;
        return policy;
    }

    public static CouponPolicyDocument of(
        final String id,
        final String name,
        final String description,
        final String key,
        final CouponPolicyType type,
        final CouponPolicyStatus status,
        final CouponBenefitCondition benefitCondition,
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition,
        final CouponApplyCondition applyCondition,
        final CouponLifecycleCondition lifecycleCondition,
        final CouponAccountCondition accountCondition,
        final String createdBy,
        final Instant createdAt,
        final String updatedBy,
        final Instant updatedAt
    ) {
        final CouponPolicyDocument policy = new CouponPolicyDocument();
        policy.id = id;
        policy.name = name;
        policy.description = description;
        policy.key = key;
        policy.type = type;
        policy.status = status;
        policy.benefitCondition = benefitCondition;
        policy.issueCondition = issueCondition;
        policy.usageCondition = usageCondition;
        policy.applyCondition = applyCondition;
        policy.lifecycleCondition = lifecycleCondition;
        policy.accountCondition = accountCondition;
        policy.createdBy = createdBy;
        policy.createdAt = createdAt;
        policy.updatedBy = updatedBy;
        policy.updatedAt = updatedAt;
        return policy;
    }

    public CouponPolicyDocument update(
        final String name,
        final String description,
        final String key,
        final CouponPolicyType type,
        final CouponBenefitCondition benefitCondition,
        final CouponIssueCondition issueCondition,
        final CouponUsageCondition usageCondition,
        final CouponApplyCondition applyCondition,
        final CouponLifecycleCondition lifecycleCondition,
        final CouponAccountCondition accountCondition,
        final String updatedBy
    ) {
        this.name = name;
        this.description = description;
        this.key = key;
        this.type = type;
        this.benefitCondition = Objects.requireNonNull(benefitCondition);
        this.issueCondition = Objects.requireNonNull(issueCondition);
        this.usageCondition = Objects.requireNonNull(usageCondition);
        this.applyCondition = Objects.requireNonNull(applyCondition);
        this.lifecycleCondition = Objects.requireNonNull(lifecycleCondition);
        this.accountCondition = Objects.requireNonNull(accountCondition);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public CouponPolicyDocument changeStatus(final CouponPolicyStatus status, final String updatedBy) {
        this.status = status;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public CouponPolicyType getType() {
        return type;
    }

    public CouponPolicyStatus getStatus() {
        return status;
    }

    public CouponBenefitCondition getBenefitCondition() {
        return benefitCondition;
    }

    public CouponIssueCondition getIssueCondition() {
        return issueCondition;
    }

    public CouponUsageCondition getUsageCondition() {
        return usageCondition;
    }

    public CouponApplyCondition getApplyCondition() {
        return applyCondition;
    }

    public CouponLifecycleCondition getLifecycleCondition() {
        return lifecycleCondition;
    }

    public CouponAccountCondition getAccountCondition() {
        return accountCondition;
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
