package im.dangmoo.benefit.infrastructure.data.point.transaction;

import im.dangmoo.benefit.infrastructure.support.mongo.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collection;

@Document(collection = MongoDocuments.POINT_TRANSACTIONS)
public class PointTransactionDocument {

    @Id
    private String id;
    private String userId;
    private PointTransactionType type;
    private long amount;
    private String policyId;
    private String policyKey;
    @Indexed
    private Instant expiresAt;
    private String orderId;
    private String originalTransactionId;
    @Indexed(unique = true, sparse = true)
    private String idempotencyKey;
    private Instant transactionAt;
    private String createdBy;
    private Instant createdAt;

    public static final String USER_ID = "userId";
    public static final String POLICY_ID = "policyId";
    public static final String TYPE = "type";
    public static final String TRANSACTION_AT = "transactionAt";
    public static final String IDEMPOTENCY_KEY = "idempotencyKey";

    private PointTransactionDocument() {
    }

    private static String reclaimKey(final String idempotencyKey) {
        return PointTransactionType.RECLAIM.name() + ":" + idempotencyKey;
    }

    public static String useKey(final String orderId) {
        return PointTransactionType.USE.name() + ":" + orderId;
    }

    private static String useCancelKey(final String orderId) {
        return PointTransactionType.USE_CANCEL.name() + ":" + orderId;
    }

    private static String expireKey(final String userId, final Instant expiresAt) {
        return PointTransactionType.EXPIRE.name() + ":" + userId + ":" + expiresAt;
    }

    public static Query query(final String userId, final String policyId) {
        final Query query = new Query();
        if (StringUtils.hasText(userId)) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        if (StringUtils.hasText(policyId)) {
            query.addCriteria(Criteria.where(POLICY_ID).is(policyId));
        }
        return query.with(Sort.by(Sort.Direction.DESC, TRANSACTION_AT));
    }

    public static Query queryByUserIdAndTypes(
        final String userId,
        final Collection<PointTransactionType> types
    ) {
        return Query.query(
            Criteria.where(USER_ID).is(userId).and(TYPE).in(types)
        ).with(Sort.by(Sort.Direction.DESC, TRANSACTION_AT));
    }

    public static Query queryByIdempotencyKey(final String idempotencyKey) {
        return Query.query(Criteria.where(IDEMPOTENCY_KEY).is(idempotencyKey));
    }

    public static PointTransactionDocument grant(
        final String userId,
        final String policyId,
        final String policyKey,
        final long amount,
        final Instant expiresAt,
        final String idempotencyKey,
        final String createdBy
    ) {
        return base(userId, PointTransactionType.GRANT, amount, createdBy, idempotencyKey)
            .policy(policyId, policyKey)
            .expires(expiresAt);
    }

    public static PointTransactionDocument reclaim(
        final String userId,
        final String policyId,
        final String policyKey,
        final long amount,
        final String idempotencyKey,
        final String createdBy
    ) {
        return base(
            userId,
            PointTransactionType.RECLAIM,
            amount,
            createdBy,
            reclaimKey(idempotencyKey)
        ).policy(policyId, policyKey);
    }

    public static PointTransactionDocument use(
        final String userId,
        final long amount,
        final String orderId,
        final String createdBy
    ) {
        final PointTransactionDocument tx = base(
            userId,
            PointTransactionType.USE,
            amount,
            createdBy,
            useKey(orderId)
        );
        tx.orderId = orderId;
        return tx;
    }

    public static PointTransactionDocument useCancel(
        final String userId,
        final long amount,
        final String orderId,
        final String originalTransactionId,
        final String createdBy
    ) {
        final PointTransactionDocument tx = base(
            userId,
            PointTransactionType.USE_CANCEL,
            amount,
            createdBy,
            useCancelKey(orderId)
        );
        tx.orderId = orderId;
        tx.originalTransactionId = originalTransactionId;
        return tx;
    }

    public static PointTransactionDocument expire(
        final String userId,
        final long amount,
        final Instant expiresAt,
        final String createdBy
    ) {
        return base(userId, PointTransactionType.EXPIRE, amount, createdBy, expireKey(userId, expiresAt))
            .expires(expiresAt);
    }

    private static PointTransactionDocument base(
        final String userId,
        final PointTransactionType type,
        final long amount,
        final String createdBy,
        final String idempotencyKey
    ) {
        final Instant now = Instant.now();
        final PointTransactionDocument tx = new PointTransactionDocument();
        tx.userId = userId;
        tx.type = type;
        tx.amount = amount;
        tx.idempotencyKey = idempotencyKey;
        tx.transactionAt = now;
        tx.createdBy = createdBy;
        tx.createdAt = now;
        return tx;
    }

    private PointTransactionDocument policy(final String policyId, final String policyKey) {
        this.policyId = policyId;
        this.policyKey = policyKey;
        return this;
    }

    private PointTransactionDocument expires(final Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public PointTransactionType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
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
}
