package im.dangmoo.benefit.infrastructure.data.coupon.code;

import im.dangmoo.benefit.infrastructure.collection.mongo.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.COUPON_CODES)
public class CouponCode {

    @Id
    private String id;
    private String policyId;
    private String policyKey;
    @Indexed(unique = true)
    private String code;
    private CouponCodeType type;
    private CouponCodeStatus status;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String CODE = "code";
    public static final String POLICY_ID = "policyId";
    public static final String STATUS = "status";

    private CouponCode() {
    }

    public static Query queryByCode(final String code) {
        return Query.query(Criteria.where(CODE).is(code));
    }

    public static Query queryByPolicyId(final String policyId) {
        return Query.query(Criteria.where(POLICY_ID).is(policyId));
    }

    public static CouponCode create(
        final String policyId,
        final String policyKey,
        final String code,
        final CouponCodeType type,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final CouponCode entity = new CouponCode();
        entity.policyId = policyId;
        entity.policyKey = Objects.requireNonNull(policyKey);
        entity.code = code;
        entity.type = type;
        entity.status = CouponCodeStatus.AVAILABLE;
        entity.createdBy = createdBy;
        entity.createdAt = now;
        entity.updatedBy = createdBy;
        entity.updatedAt = now;
        return entity;
    }

    public boolean isAvailable() {
        return status == CouponCodeStatus.AVAILABLE;
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

    public String getCode() {
        return code;
    }

    public CouponCodeType getType() {
        return type;
    }

    public CouponCodeStatus getStatus() {
        return status;
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
