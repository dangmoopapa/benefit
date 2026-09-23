package im.dangmoo.benefit.data.entity.membership.history;

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

@Document(collection = MongoDocuments.MEMBERSHIP_BENEFIT_HISTORIES)
public class MembershipBenefitHistoryDocument {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed(unique = true)
    private String orderId;
    private String contractId;
    private String policyId;
    private MembershipSeason season;
    private MembershipBenefitApplied applied;
    private MembershipBenefitHistoryStatus status;
    private Instant transactionAt;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String USER_ID = "userId";
    public static final String ORDER_ID = "orderId";
    public static final String CONTRACT_ID = "contractId";
    public static final String TRANSACTION_AT = "transactionAt";

    private MembershipBenefitHistoryDocument() {
    }

    public static Query queryByOrderId(final String orderId) {
        return Query.query(Criteria.where(ORDER_ID).is(orderId));
    }

    public static Query query(
        final String userId,
        final String contractId
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(userId)) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        if (StringUtils.hasText(contractId)) {
            query.addCriteria(Criteria.where(CONTRACT_ID).is(contractId));
        }
        return query;
    }

    public static MembershipBenefitHistoryDocument apply(
        final String userId,
        final String orderId,
        final String contractId,
        final String policyId,
        final MembershipSeason season,
        final MembershipBenefitApplied applied,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final MembershipBenefitHistoryDocument history = new MembershipBenefitHistoryDocument();
        history.userId = Objects.requireNonNull(userId);
        history.orderId = Objects.requireNonNull(orderId);
        history.contractId = Objects.requireNonNull(contractId);
        history.policyId = Objects.requireNonNull(policyId);
        history.season = Objects.requireNonNull(season);
        history.applied = Objects.requireNonNull(applied);
        history.status = MembershipBenefitHistoryStatus.APPLIED;
        history.transactionAt = now;
        history.createdBy = createdBy;
        history.createdAt = now;
        history.updatedBy = createdBy;
        history.updatedAt = now;
        return history;
    }

    public MembershipBenefitHistoryDocument cancel(final String updatedBy) {
        this.status = MembershipBenefitHistoryStatus.CANCELLED;
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

    public String getOrderId() {
        return orderId;
    }

    public String getContractId() {
        return contractId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public MembershipSeason getSeason() {
        return season;
    }

    public MembershipBenefitApplied getApplied() {
        return applied;
    }

    public MembershipBenefitHistoryStatus getStatus() {
        return status;
    }

    public Instant getTransactionAt() {
        return transactionAt;
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
