package im.dangmoo.benefit.domain.data.membership.subscription;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPeriod;
import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;

@Document(collection = MongoCollections.MEMBERSHIP_SUBSCRIPTIONS)
public class MembershipSubscription {

    @Id
    private String id;
    private String userId;
    private String policyId;
    private MembershipPeriod period;
    private String updatedBy;
    private Instant updatedAt;

    private static final String USER_ID = "userId";

    private MembershipSubscription() {
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static MembershipSubscription join(
        final String userId,
        final String policyId,
        final MembershipPeriod period
    ) {
        final MembershipSubscription entity = new MembershipSubscription();
        entity.userId = userId;
        entity.policyId = policyId;
        entity.period = period;
        return entity;
    }

    public MembershipSubscription renew(final String policyId, final MembershipPeriod period) {
        this.policyId = policyId;
        this.period = period;
        return this;
    }

    public MembershipSubscription cancel(final Instant now, final String updatedBy) {
        if (this.period != null) {
            this.period.close(now);
        }
        this.updatedBy = updatedBy;
        this.updatedAt = now;
        return this;
    }

    public boolean isEffective(final Instant now) {
        return period != null && period.contains(now);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public MembershipPeriod getPeriod() {
        return period;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
