package im.dangmoo.benefit.domain.data.point.policy;

import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Document(collection = MongoCollections.POINT_POLICIES)
public class PointPolicy {

    @Id
    private String id;
    private String code;
    private String name;
    private String description;
    private String platformId;
    private PointPolicyStatus status;
    private PointExpirationCondition expirationCondition;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String PLATFORM_ID = "platformId";
    private static final String STATUS = "status";

    private PointPolicy() {
    }

    public static Query query(
        final String code,
        final String name,
        final String platformId,
        final PointPolicyStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(code)) {
            query.addCriteria(Criteria.where(CODE).is(code));
        }
        if (StringUtils.hasText(name)) {
            final PointPolicy probe = new PointPolicy();
            probe.name = name;
            final ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher(NAME, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
            query.addCriteria(new Criteria().alike(Example.of(probe, matcher)));
        }
        if (StringUtils.hasText(platformId)) {
            query.addCriteria(Criteria.where(PLATFORM_ID).is(platformId));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByCode(final String code) {
        return Query.query(Criteria.where(CODE).is(code));
    }

    public static PointPolicy create(
        final String code,
        final String name,
        final String description,
        final String platformId,
        final PointExpirationCondition expirationCondition,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final PointPolicy document = new PointPolicy();
        document.code = code;
        document.name = name;
        document.description = description;
        document.platformId = platformId;
        document.status = PointPolicyStatus.DRAFT;
        document.expirationCondition = expirationCondition;
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
        final PointExpirationCondition expirationCondition,
        final String updatedBy
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.platformId = platformId;
        this.expirationCondition = expirationCondition;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void activate(final String updatedBy) {
        this.status = PointPolicyStatus.ACTIVE;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void suspend(final String updatedBy) {
        this.status = PointPolicyStatus.SUSPENDED;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == PointPolicyStatus.ACTIVE;
    }

    public Instant resolveExpiresAt(final Instant issuedAt) {
        if (expirationCondition == null) {
            return null;
        }
        return expirationCondition.resolveExpiresAt(issuedAt);
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

    public PointPolicyStatus getStatus() {
        return status;
    }

    public PointExpirationCondition getExpirationCondition() {
        return expirationCondition;
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
