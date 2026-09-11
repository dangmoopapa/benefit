package im.dangmoo.benefit.domain.data.membership.policy;

import im.dangmoo.benefit.domain.data.membership.policy.privilege.MembershipPrivilege;
import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;

@Document(collection = MongoCollections.MEMBERSHIP_POLICIES)
public class MembershipPolicy {

    @Id
    private String id;
    private int version;
    private MembershipPrivilege privilege;
    private MembershipPeriod period;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    private static final String VERSION = "version";

    private MembershipPolicy() {
    }

    public static Query query(final Integer version) {
        final Query query = new Query();
        if (version != null) {
            query.addCriteria(Criteria.where(VERSION).is(version));
        }
        return query;
    }

    public static Query queryByVersion(final int version) {
        return Query.query(Criteria.where(VERSION).is(version));
    }

    public static Query queryLatest() {
        return new Query().with(Sort.by(Sort.Direction.DESC, VERSION)).limit(1);
    }

    public static MembershipPolicy create(
        final int version,
        final MembershipPrivilege privilege,
        final MembershipPeriod period,
        final String createdBy
    ) {
        final Instant now = TimeUtils.now();
        final MembershipPolicy entity = new MembershipPolicy();
        entity.version = version;
        entity.privilege = privilege;
        entity.period = period;
        entity.createdBy = createdBy;
        entity.createdAt = now;
        entity.updatedBy = createdBy;
        entity.updatedAt = now;
        return entity;
    }

    public MembershipPolicy update(
        final int version,
        final MembershipPrivilege privilege,
        final MembershipPeriod period,
        final String updatedBy
    ) {
        this.version = version;
        this.privilege = privilege;
        this.period = period;
        this.updatedBy = updatedBy;
        this.updatedAt = TimeUtils.now();
        return this;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public MembershipPrivilege getPrivilege() {
        return privilege;
    }

    public MembershipPeriod getPeriod() {
        return period;
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
