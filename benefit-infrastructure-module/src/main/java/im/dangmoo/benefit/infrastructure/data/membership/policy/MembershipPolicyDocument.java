package im.dangmoo.benefit.infrastructure.data.membership.policy;

import im.dangmoo.benefit.infrastructure.support.mongo.MongoDocuments;
import im.dangmoo.benefit.infrastructure.data.membership.policy.benefit.MembershipBenefit;
import im.dangmoo.benefit.infrastructure.data.membership.policy.condition.MembershipAccountCondition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.MEMBERSHIP_POLICIES)
public class MembershipPolicyDocument {

    @Id
    private String id;
    private String name;
    private String description;
    @Indexed(unique = true)
    private String key;
    private MembershipPolicyStatus status;
    private MembershipSeason season;
    private MembershipBenefit benefit;
    private MembershipAccountCondition accountCondition;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String SEASON = "season";

    private MembershipPolicyDocument() {
    }

    public static Query query(
        final String key,
        final String name,
        final MembershipPolicyStatus status,
        final MembershipSeason season
    ) {
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
        if (season != null) {
            query.addCriteria(Criteria.where(SEASON).is(season));
        }
        return query;
    }

    public static Query queryByKey(final String key) {
        return Query.query(Criteria.where(KEY).is(key));
    }

    public static MembershipPolicyDocument create(
        final String name,
        final String description,
        final String key,
        final MembershipSeason season,
        final MembershipBenefit benefit,
        final MembershipAccountCondition accountCondition,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final MembershipPolicyDocument policy = new MembershipPolicyDocument();
        policy.name = Objects.requireNonNull(name);
        policy.description = description;
        policy.key = Objects.requireNonNull(key);
        policy.status = MembershipPolicyStatus.DRAFT;
        policy.season = Objects.requireNonNull(season);
        policy.benefit = Objects.requireNonNull(benefit);
        policy.accountCondition = Objects.requireNonNull(accountCondition);
        policy.createdBy = createdBy;
        policy.createdAt = now;
        policy.updatedBy = createdBy;
        policy.updatedAt = now;
        return policy;
    }

    public MembershipPolicyDocument update(
        final String name,
        final String description,
        final MembershipSeason season,
        final MembershipBenefit benefit,
        final MembershipAccountCondition accountCondition,
        final String updatedBy
    ) {
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.season = Objects.requireNonNull(season);
        this.benefit = Objects.requireNonNull(benefit);
        this.accountCondition = Objects.requireNonNull(accountCondition);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public MembershipPolicyDocument changeStatus(final MembershipPolicyStatus status, final String updatedBy) {
        this.status = Objects.requireNonNull(status);
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

    public MembershipPolicyStatus getStatus() {
        return status;
    }

    public MembershipSeason getSeason() {
        return season;
    }

    public MembershipBenefit getBenefit() {
        return benefit;
    }

    public MembershipAccountCondition getAccountCondition() {
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
