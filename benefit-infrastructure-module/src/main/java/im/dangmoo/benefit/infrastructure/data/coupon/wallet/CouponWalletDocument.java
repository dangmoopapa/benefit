package im.dangmoo.benefit.infrastructure.data.coupon.wallet;

import im.dangmoo.benefit.infrastructure.support.mongo.MongoDocuments;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Document(collection = MongoDocuments.COUPON_WALLETS)
public class CouponWalletDocument {

    @Id
    private String id;
    private String userId;
    private String orderId;
    private String policyId;
    private String policyKey;
    @Indexed(unique = true)
    private String idempotencyKey;
    private CouponWalletStatus status;
    private BigDecimal usedAmount;
    private Instant issuedAt;
    private Instant expiresAt;
    private Instant usedAt;
    private Instant recoveredAt;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String USER_ID = "userId";
    public static final String ORDER_ID = "orderId";
    public static final String POLICY_ID = "policyId";
    public static final String IDEMPOTENCY_KEY = "idempotencyKey";
    public static final String STATUS = "status";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_BY = "updatedBy";

    private CouponWalletDocument() {
    }

    public static Query query(
        final String userId,
        final String orderId,
        final String policyId,
        final String createdBy,
        final String updatedBy,
        final CouponWalletStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(userId)) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        if (StringUtils.hasText(orderId)) {
            query.addCriteria(Criteria.where(ORDER_ID).is(orderId));
        }
        if (StringUtils.hasText(policyId)) {
            query.addCriteria(Criteria.where(POLICY_ID).is(policyId));
        }
        if (StringUtils.hasText(createdBy)) {
            query.addCriteria(Criteria.where(CREATED_BY).is(createdBy));
        }
        if (StringUtils.hasText(updatedBy)) {
            query.addCriteria(Criteria.where(UPDATED_BY).is(updatedBy));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static Query queryByUserIdAndStatus(final String userId, final CouponWalletStatus status) {
        return Query.query(Criteria.where(USER_ID).is(userId).and(STATUS).is(status));
    }

    public static Query queryByIdempotencyKey(final String idempotencyKey) {
        return Query.query(Criteria.where(IDEMPOTENCY_KEY).is(idempotencyKey));
    }

    public static CouponWalletDocument create(
        final String userId,
        final String policyId,
        final String policyKey,
        final String idempotencyKey,
        final Instant expiresAt,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final CouponWalletDocument wallet = new CouponWalletDocument();
        wallet.id = new ObjectId().toHexString();
        wallet.userId = userId;
        wallet.policyId = policyId;
        wallet.policyKey = Objects.requireNonNull(policyKey);
        wallet.idempotencyKey = idempotencyKey;
        wallet.status = CouponWalletStatus.AVAILABLE;
        wallet.issuedAt = now;
        wallet.expiresAt = expiresAt;
        wallet.createdBy = createdBy;
        wallet.createdAt = now;
        wallet.updatedBy = createdBy;
        wallet.updatedAt = now;
        return wallet;
    }

    public static CouponWalletDocument of(
        final String id,
        final String userId,
        final String policyId,
        final String policyKey,
        final String idempotencyKey,
        final CouponWalletStatus status,
        final Instant issuedAt,
        final Instant expiresAt,
        final String createdBy,
        final Instant createdAt,
        final String updatedBy,
        final Instant updatedAt
    ) {
        final CouponWalletDocument wallet = new CouponWalletDocument();
        wallet.id = id;
        wallet.userId = userId;
        wallet.policyId = policyId;
        wallet.policyKey = policyKey;
        wallet.idempotencyKey = idempotencyKey;
        wallet.status = status;
        wallet.issuedAt = issuedAt;
        wallet.expiresAt = expiresAt;
        wallet.createdBy = createdBy;
        wallet.createdAt = createdAt;
        wallet.updatedBy = updatedBy;
        wallet.updatedAt = updatedAt;
        return wallet;
    }

    public CouponWalletDocument use(final String orderId, final BigDecimal usedAmount, final String updatedBy) {
        final Instant now = Instant.now();
        this.status = CouponWalletStatus.USED;
        this.orderId = orderId;
        this.usedAmount = usedAmount;
        this.usedAt = now;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
        return this;
    }

    public CouponWalletDocument recover(final String updatedBy) {
        final Instant now = Instant.now();
        this.status = CouponWalletStatus.RECOVERED;
        this.recoveredAt = now;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
        return this;
    }

    public boolean isAvailable(final Instant now) {
        if (status != CouponWalletStatus.AVAILABLE) {
            return false;
        }
        return expiresAt == null || !now.isAfter(expiresAt);
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

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public CouponWalletStatus getStatus() {
        return status;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public Instant getRecoveredAt() {
        return recoveredAt;
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
