package im.dangmoo.benefit.data.entity.promotion.applier;

import im.dangmoo.benefit.data.infrastructure.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.PROMOTION_APPLIERS)
@CompoundIndex(name = "uk_policyId_userId", def = "{'policyId': 1, 'userId': 1}", unique = true)
public class PromotionApplierDocument {

    @Id
    private String id;
    private String policyId;
    private String policyKey;
    private String userId;
    private PromotionApplierStatus status;
    private Instant appliedAt;

    public static final String POLICY_ID = "policyId";
    public static final String USER_ID = "userId";

    private PromotionApplierDocument() {
    }

    public static Query queryByPolicyIdAndUserId(final String policyId, final String userId) {
        return Query.query(
            Criteria.where(POLICY_ID).is(policyId).and(USER_ID).is(userId)
        );
    }

    public static Query queryByPolicyId(final String policyId) {
        return Query.query(Criteria.where(POLICY_ID).is(policyId));
    }

    public static PromotionApplierDocument apply(
        final String policyId,
        final String policyKey,
        final String userId
    ) {
        final PromotionApplierDocument applier = new PromotionApplierDocument();
        applier.policyId = Objects.requireNonNull(policyId);
        applier.policyKey = Objects.requireNonNull(policyKey);
        applier.userId = Objects.requireNonNull(userId);
        applier.status = PromotionApplierStatus.APPLIED;
        applier.appliedAt = Instant.now();
        return applier;
    }

    public String getId() {
        return id;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public String getUserId() {
        return userId;
    }

    public PromotionApplierStatus getStatus() {
        return status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }
}
