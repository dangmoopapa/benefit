package im.dangmoo.benefit.domain.data.coupon.wallet;

import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import im.dangmoo.benefit.domain.util.TimeUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = MongoCollections.COUPON_WALLETS)
public class CouponWallet {

    @Id
    private String id;
    private String userId;
    private String orderId;
    private String policyId;
    private String policyCode;
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
    @Version
    private Long version;

    private static final String USER_ID = "userId";
    private static final String POLICY_ID = "policyId";
    private static final String ISSUED_AT = "issuedAt";

    private CouponWallet() {
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static Query queryByPolicyId(final String policyId) {
        return Query.query(Criteria.where(POLICY_ID).is(policyId))
            .with(Sort.by(Sort.Direction.DESC, ISSUED_AT));
    }

    public static CouponWallet create(
        final String userId,
        final String policyId,
        final String policyCode,
        final String idempotencyKey,
        final Instant expiresAt,
        final String createdBy
    ) {
        final Instant now = TimeUtils.now();
        final CouponWallet entity = new CouponWallet();
        entity.userId = userId;
        entity.policyId = policyId;
        entity.policyCode = policyCode;
        entity.idempotencyKey = idempotencyKey;
        entity.status = CouponWalletStatus.AVAILABLE;
        entity.issuedAt = now;
        entity.expiresAt = expiresAt;
        entity.createdBy = createdBy;
        entity.createdAt = now;
        entity.updatedBy = createdBy;
        entity.updatedAt = now;
        return entity;
    }

    public CouponWallet use(final String orderId, final BigDecimal usedAmount, final String updatedBy) {
        if (status != CouponWalletStatus.AVAILABLE) {
            throw new IllegalStateException("wallet is not available");
        }
        final Instant now = TimeUtils.now();
        this.status = CouponWalletStatus.USED;
        this.orderId = orderId;
        this.usedAmount = usedAmount;
        this.usedAt = now;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
        return this;
    }

    public CouponWallet recover(final String updatedBy) {
        if (status != CouponWalletStatus.USED) {
            throw new IllegalStateException("wallet is not used");
        }
        final Instant now = TimeUtils.now();
        this.status = CouponWalletStatus.RECOVERED;
        this.recoveredAt = now;
        this.updatedBy = updatedBy;
        this.updatedAt = now;
        return this;
    }

    public boolean isNotAvailable() {
        return status != CouponWalletStatus.AVAILABLE;
    }

    public boolean isUsableAt(final Instant now) {
        if (isNotAvailable()) {
            return false;
        }
        return expiresAt == null || !now.isAfter(expiresAt);
    }

    public boolean isUsed() {
        return status == CouponWalletStatus.USED;
    }

    public boolean belongsTo(final String ownerUserId) {
        return ownerUserId == null || userId.equals(ownerUserId);
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

    public String getPolicyCode() {
        return policyCode;
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
