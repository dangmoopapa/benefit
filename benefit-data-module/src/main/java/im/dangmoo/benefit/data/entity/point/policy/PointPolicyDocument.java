package im.dangmoo.benefit.data.entity.point.policy;

import im.dangmoo.benefit.data.infrastructure.MongoDocuments;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointAccountCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointBenefitCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointExpireCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointIssueCondition;
import im.dangmoo.benefit.data.entity.point.policy.condition.PointLifecycleCondition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.POINT_POLICIES)
public class PointPolicyDocument {

    @Id
    private String id;
    private String name;
    private String description;
    @Indexed(unique = true)
    private String key;
    private PointPolicyStatus status;
    private PointBenefitCondition benefitCondition;
    private PointIssueCondition issueCondition;
    private PointExpireCondition expireCondition;
    private PointLifecycleCondition lifecycleCondition;
    private PointAccountCondition accountCondition;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String STATUS = "status";

    private PointPolicyDocument() {
    }

    public static Query query(final String key, final String name, final PointPolicyStatus status) {
        final Query query = new Query();
        if (StringUtils.hasText(key)) {
            query.addCriteria(Criteria.where(KEY).is(key));
        }
        if (StringUtils.hasText(name)) {
            query.addCriteria(Criteria.where(NAME).regex(name, "i"));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByKey(final String key) {
        return Query.query(Criteria.where(KEY).is(key));
    }

    public static PointPolicyDocument create(
        final String name,
        final String description,
        final String key,
        final PointBenefitCondition benefitCondition,
        final PointIssueCondition issueCondition,
        final PointExpireCondition expireCondition,
        final PointLifecycleCondition lifecycleCondition,
        final PointAccountCondition accountCondition,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final PointPolicyDocument policy = new PointPolicyDocument();
        policy.name = name;
        policy.description = description;
        policy.key = key;
        policy.status = PointPolicyStatus.DRAFT;
        policy.benefitCondition = Objects.requireNonNull(benefitCondition);
        policy.issueCondition = Objects.requireNonNull(issueCondition);
        policy.expireCondition = Objects.requireNonNull(expireCondition);
        policy.lifecycleCondition = Objects.requireNonNull(lifecycleCondition);
        policy.accountCondition = Objects.requireNonNull(accountCondition);
        policy.createdBy = createdBy;
        policy.createdAt = now;
        policy.updatedBy = createdBy;
        policy.updatedAt = now;
        return policy;
    }

    public PointPolicyDocument update(
        final String name,
        final String description,
        final String key,
        final PointBenefitCondition benefitCondition,
        final PointIssueCondition issueCondition,
        final PointExpireCondition expireCondition,
        final PointLifecycleCondition lifecycleCondition,
        final PointAccountCondition accountCondition,
        final String updatedBy
    ) {
        this.name = name;
        this.description = description;
        this.key = key;
        this.benefitCondition = Objects.requireNonNull(benefitCondition);
        this.issueCondition = Objects.requireNonNull(issueCondition);
        this.expireCondition = Objects.requireNonNull(expireCondition);
        this.lifecycleCondition = Objects.requireNonNull(lifecycleCondition);
        this.accountCondition = Objects.requireNonNull(accountCondition);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public PointPolicyDocument changeStatus(final PointPolicyStatus status, final String updatedBy) {
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

    public PointPolicyStatus getStatus() {
        return status;
    }

    public PointBenefitCondition getBenefitCondition() {
        return benefitCondition;
    }

    public PointIssueCondition getIssueCondition() {
        return issueCondition;
    }

    public PointExpireCondition getExpireCondition() {
        return expireCondition;
    }

    public PointLifecycleCondition getLifecycleCondition() {
        return lifecycleCondition;
    }

    public PointAccountCondition getAccountCondition() {
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
