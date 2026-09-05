package im.dangmoo.benefit.domain.coupon.wallet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "coupon_wallets")
public class CouponWallet {

    @Id
    private String id;
    private String userId;
    private String orderId;
    private String policyId;
    private String policyCode;
    private CouponWalletStatus status;
    private BigDecimal usedAmount;
    private Instant issuedAt;
    private Instant expiresAt;
    private Instant usedAt;
    private Instant recoveredAt;
    private Instant createdAt;
    private Instant updatedAt;

    private static final String USER_ID = "userId";
    private static final String POLICY_ID = "policyId";

    private CouponWallet() {
    }

    public static Query queryByUserId(final String userId) {
        return Query.query(Criteria.where(USER_ID).is(userId));
    }

    public static Query queryByPolicyId(final String policyId) {
        return Query.query(Criteria.where(POLICY_ID).is(policyId));
    }

    public static Query queryByUserIdAndPolicyId(final String userId, final String policyId) {
        return Query.query(Criteria.where(USER_ID).is(userId).and(POLICY_ID).is(policyId));
    }

    public static CouponWallet create(
        final String userId,
        final String policyId,
        final String policyCode,
        final Instant expiresAt
    ) {
        final Instant now = Instant.now();
        final CouponWallet document = new CouponWallet();
        document.userId = userId;
        document.policyId = policyId;
        document.policyCode = policyCode;
        document.status = CouponWalletStatus.AVAILABLE;
        document.issuedAt = now;
        document.expiresAt = expiresAt;
        document.createdAt = now;
        document.updatedAt = now;
        return document;
    }

    public void use(final String orderId, final BigDecimal usedAmount) {
        this.status = CouponWalletStatus.USED;
        this.orderId = orderId;
        this.usedAmount = usedAmount;
        this.usedAt = Instant.now();
        this.updatedAt = this.usedAt;
    }

    public void recover() {
        this.status = CouponWalletStatus.RECOVERED;
        this.recoveredAt = Instant.now();
        this.updatedAt = this.recoveredAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
