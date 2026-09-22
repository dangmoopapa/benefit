package im.dangmoo.benefit.infrastructure.data.promotion.policy;

import im.dangmoo.benefit.infrastructure.collection.mongo.MongoDocuments;
import im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionFeature;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = MongoDocuments.PROMOTION_POLICIES)
public class PromotionPolicy {

    @Id
    private String id;
    @Indexed(unique = true)
    private String key;
    private String thumbnailImageUrl;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private PromotionPolicyStatus status;
    private String content;
    private String disclaimer;
    private List<PromotionFeature> features = new ArrayList<>();
    private Integer sortOrder;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String KEY = "key";
    public static final String TITLE = "title";
    public static final String STATUS = "status";
    public static final String START_AT = "startAt";
    public static final String END_AT = "endAt";
    public static final String SORT_ORDER = "sortOrder";

    private PromotionPolicy() {
    }

    public static Query query(
        final String key,
        final String title,
        final PromotionPolicyStatus status
    ) {
        final Query query = new Query();
        if (StringUtils.hasText(key)) {
            query.addCriteria(Criteria.where(KEY).is(key));
        }
        if (StringUtils.hasText(title)) {
            query.addCriteria(Criteria.where(TITLE).regex(title, "i"));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByKey(final String key) {
        return Query.query(Criteria.where(KEY).is(key));
    }

    public static Query queryActiveList(final Instant now) {
        return Query.query(
            Criteria.where(STATUS).is(PromotionPolicyStatus.ACTIVE)
                .and(START_AT).lte(now)
                .and(END_AT).gte(now)
        );
    }

    public static Query queryEndedForAutoLottery(final Instant now) {
        return Query.query(
            Criteria.where(END_AT).lte(now)
                .and("features.type").is("ENTRY")
                .and("features.entry.lotteryType").is("AUTO_COUNT")
        );
    }

    public static PromotionPolicy create(
        final String key,
        final String thumbnailImageUrl,
        final String title,
        final String description,
        final Instant startAt,
        final Instant endAt,
        final String content,
        final String disclaimer,
        final List<PromotionFeature> features,
        final Integer sortOrder,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final PromotionPolicy policy = new PromotionPolicy();
        policy.key = Objects.requireNonNull(key);
        policy.thumbnailImageUrl = thumbnailImageUrl;
        policy.title = Objects.requireNonNull(title);
        policy.description = description;
        policy.startAt = Objects.requireNonNull(startAt);
        policy.endAt = Objects.requireNonNull(endAt);
        policy.status = PromotionPolicyStatus.DRAFT;
        policy.content = content;
        policy.disclaimer = disclaimer;
        policy.features = features == null ? new ArrayList<>() : new ArrayList<>(features);
        policy.sortOrder = sortOrder == null ? 0 : sortOrder;
        policy.createdBy = createdBy;
        policy.createdAt = now;
        policy.updatedBy = createdBy;
        policy.updatedAt = now;
        return policy;
    }

    public PromotionPolicy update(
        final String thumbnailImageUrl,
        final String title,
        final String description,
        final Instant startAt,
        final Instant endAt,
        final String content,
        final String disclaimer,
        final List<PromotionFeature> features,
        final Integer sortOrder,
        final String updatedBy
    ) {
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.startAt = Objects.requireNonNull(startAt);
        this.endAt = Objects.requireNonNull(endAt);
        this.content = content;
        this.disclaimer = disclaimer;
        this.features = features == null ? new ArrayList<>() : new ArrayList<>(features);
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public PromotionPolicy changeStatus(final PromotionPolicyStatus status, final String updatedBy) {
        this.status = Objects.requireNonNull(status);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public PromotionPolicyStatus getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public List<PromotionFeature> getFeatures() {
        return features;
    }

    public Integer getSortOrder() {
        return sortOrder;
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
