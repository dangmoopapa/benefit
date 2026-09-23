package im.dangmoo.benefit.data.entity.membership.contract;

import im.dangmoo.benefit.data.infrastructure.MongoDocuments;
import im.dangmoo.benefit.data.entity.membership.policy.MembershipSeason;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.MEMBERSHIP_CONTRACTS)
public class MembershipContractDocument {

    @Id
    private String id;
    @Indexed
    private String userId;
    private String policyId;
    private String policyKey;
    private MembershipSeason season;
    private MembershipContractStatus status;
    private Instant periodStart;
    private Instant periodEnd;
    private boolean cancelAtPeriodEnd;
    private boolean autoRenew;
    @Indexed(unique = true)
    private String idempotencyKey;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String USER_ID = "userId";
    public static final String POLICY_ID = "policyId";
    public static final String STATUS = "status";
    public static final String PERIOD_END = "periodEnd";
    public static final String IDEMPOTENCY_KEY = "idempotencyKey";

    private MembershipContractDocument() {
    }

    public static Query queryByIdempotencyKey(final String idempotencyKey) {
        return Query.query(Criteria.where(IDEMPOTENCY_KEY).is(idempotencyKey));
    }

    public static Query queryEffectiveByUserId(final String userId, final Instant now) {
        return Query.query(
            Criteria.where(USER_ID).is(userId)
                .and(STATUS).is(MembershipContractStatus.ACTIVE)
                .and(PERIOD_END).gt(now)
        );
    }

    public static Query query(
        final String userId,
        final String policyId,
        final MembershipContractStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(userId)) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        if (StringUtils.hasText(policyId)) {
            query.addCriteria(Criteria.where(POLICY_ID).is(policyId));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static MembershipContractDocument join(
        final String userId,
        final String policyId,
        final String policyKey,
        final MembershipSeason season,
        final Instant periodStart,
        final Instant periodEnd,
        final String idempotencyKey,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final MembershipContractDocument contract = new MembershipContractDocument();
        contract.userId = Objects.requireNonNull(userId);
        contract.policyId = Objects.requireNonNull(policyId);
        contract.policyKey = Objects.requireNonNull(policyKey);
        contract.season = Objects.requireNonNull(season);
        contract.status = MembershipContractStatus.ACTIVE;
        contract.periodStart = Objects.requireNonNull(periodStart);
        contract.periodEnd = Objects.requireNonNull(periodEnd);
        contract.cancelAtPeriodEnd = false;
        contract.autoRenew = true;
        contract.idempotencyKey = Objects.requireNonNull(idempotencyKey);
        contract.createdBy = createdBy;
        contract.createdAt = now;
        contract.updatedBy = createdBy;
        contract.updatedAt = now;
        return contract;
    }

    public MembershipContractDocument reactivate(
        final Instant periodStart,
        final Instant periodEnd,
        final String updatedBy
    ) {
        this.status = MembershipContractStatus.ACTIVE;
        this.periodStart = Objects.requireNonNull(periodStart);
        this.periodEnd = Objects.requireNonNull(periodEnd);
        this.cancelAtPeriodEnd = false;
        this.autoRenew = true;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public MembershipContractDocument scheduleCancel(final String updatedBy) {
        this.cancelAtPeriodEnd = true;
        this.autoRenew = false;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public MembershipContractDocument renew(final Instant periodEnd, final String updatedBy) {
        this.periodEnd = Objects.requireNonNull(periodEnd);
        this.cancelAtPeriodEnd = false;
        this.autoRenew = true;
        this.status = MembershipContractStatus.ACTIVE;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
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

    public String getPolicyKey() {
        return policyKey;
    }

    public MembershipSeason getSeason() {
        return season;
    }

    public MembershipContractStatus getStatus() {
        return status;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public boolean isCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
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
