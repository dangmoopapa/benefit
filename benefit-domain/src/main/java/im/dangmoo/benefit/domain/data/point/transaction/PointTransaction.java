package im.dangmoo.benefit.domain.data.point.transaction;

import im.dangmoo.benefit.domain.data.point.balance.PointLot;
import im.dangmoo.benefit.domain.infrastructure.mongo.MongoCollections;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Document(collection = MongoCollections.POINT_TRANSACTIONS)
public class PointTransaction {

    private static final String USER_ID = "userId";
    private static final String TYPE = "type";
    private static final String IDEMPOTENCY_KEY = "idempotencyKey";
    private static final String RELATED_TRANSACTION_ID = "relatedTransactionId";
    private static final String TRANSACTION_AT = "transactionAt";

    @Id
    private String id;
    private String userId;
    private PointTransactionType type;
    private long point;
    private String orderId;
    private String idempotencyKey;
    private Instant transactionAt;
    private String policyId;
    private Instant expiresAt;
    private String relatedTransactionId;
    private List<PointLot> usedLots;
    private String createdBy;
    private Instant createdAt;

    private PointTransaction() {
    }

    public static Query queryByIdempotencyKey(final String idempotencyKey) {
        return Query.query(Criteria.where(IDEMPOTENCY_KEY).is(idempotencyKey));
    }

    public static Query queryByRelatedTransactionId(final String relatedTransactionId) {
        return Query.query(Criteria.where(RELATED_TRANSACTION_ID).is(relatedTransactionId));
    }

    public static Query queryByUserId(
        final String userId,
        final Collection<PointTransactionType> types,
        final Instant beforeTransactionAt,
        final String beforeId
    ) {
        final Criteria criteria = Criteria.where(USER_ID).is(userId);
        if (types != null && !types.isEmpty()) {
            criteria.and(TYPE).in(types);
        }
        if (beforeTransactionAt != null && beforeId != null) {
            criteria.andOperator(
                new Criteria().orOperator(
                    Criteria.where(TRANSACTION_AT).lt(beforeTransactionAt),
                    new Criteria().andOperator(
                        Criteria.where(TRANSACTION_AT).is(beforeTransactionAt),
                        Criteria.where("_id").lt(beforeId)
                    )
                )
            );
        }
        return Query.query(criteria);
    }

    public static String transactionAtField() {
        return TRANSACTION_AT;
    }

    public static String idField() {
        return "_id";
    }

    public static PointTransaction issued(
        final String userId,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final String policyId,
        final Instant expiresAt,
        final String createdBy,
        final Instant now
    ) {
        final PointTransaction document = base(userId, PointTransactionType.ISSUED, point, orderId, idempotencyKey, createdBy, now);
        document.policyId = policyId;
        document.expiresAt = expiresAt;
        return document;
    }

    public static PointTransaction revoked(
        final String userId,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final String relatedTransactionId,
        final String policyId,
        final Instant expiresAt,
        final String createdBy,
        final Instant now
    ) {
        final PointTransaction document = base(userId, PointTransactionType.REVOKED, point, orderId, idempotencyKey, createdBy, now);
        document.relatedTransactionId = relatedTransactionId;
        document.policyId = policyId;
        document.expiresAt = expiresAt;
        return document;
    }

    public static PointTransaction used(
        final String userId,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final List<PointLot> usedLots,
        final String createdBy,
        final Instant now
    ) {
        final PointTransaction document = base(userId, PointTransactionType.USED, point, orderId, idempotencyKey, createdBy, now);
        document.usedLots = usedLots;
        return document;
    }

    public static PointTransaction restored(
        final String userId,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final String relatedTransactionId,
        final List<PointLot> usedLots,
        final String createdBy,
        final Instant now
    ) {
        final PointTransaction document = base(userId, PointTransactionType.RESTORED, point, orderId, idempotencyKey, createdBy, now);
        document.relatedTransactionId = relatedTransactionId;
        document.usedLots = usedLots;
        return document;
    }

    private static PointTransaction base(
        final String userId,
        final PointTransactionType type,
        final long point,
        final String orderId,
        final String idempotencyKey,
        final String createdBy,
        final Instant now
    ) {
        final PointTransaction document = new PointTransaction();
        document.userId = userId;
        document.type = type;
        document.point = point;
        document.orderId = orderId;
        document.idempotencyKey = idempotencyKey;
        document.transactionAt = now;
        document.createdBy = createdBy;
        document.createdAt = now;
        return document;
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

    public long getPoint() {
        return point;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getTransactionAt() {
        return transactionAt;
    }

    public String getPolicyId() {
        return policyId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getRelatedTransactionId() {
        return relatedTransactionId;
    }

    public List<PointLot> getUsedLots() {
        return usedLots == null ? List.of() : List.copyOf(usedLots);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
