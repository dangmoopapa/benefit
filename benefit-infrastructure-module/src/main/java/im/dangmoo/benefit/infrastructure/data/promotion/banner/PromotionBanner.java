package im.dangmoo.benefit.infrastructure.data.promotion.banner;

import im.dangmoo.benefit.infrastructure.collection.mongo.MongoDocuments;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Document(collection = MongoDocuments.PROMOTION_BANNERS)
public class PromotionBanner {

    @Id
    private String id;
    @Indexed(unique = true)
    private String key;
    private String name;
    private PromotionBannerStatus status;
    private List<PromotionBannerItem> items = new ArrayList<>();
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String STATUS = "status";

    private PromotionBanner() {
    }

    public static Query query(final String key, final String name, final PromotionBannerStatus status) {
        final Query query = new Query();
        if (StringUtils.hasText(key)) {
            query.addCriteria(Criteria.where(KEY).is(key));
        }
        if (StringUtils.hasText(name)) {
            query.addCriteria(Criteria.where(NAME).regex(name, "i"));
        }
        if (status != null) {
            query.addCriteria(Criteria.where(STATUS).is(status));
        }
        return query;
    }

    public static Query queryByKey(final String key) {
        return Query.query(Criteria.where(KEY).is(key));
    }

    public static PromotionBanner create(
        final String key,
        final String name,
        final List<PromotionBannerItem> items,
        final String createdBy
    ) {
        final Instant now = Instant.now();
        final PromotionBanner banner = new PromotionBanner();
        banner.key = Objects.requireNonNull(key);
        banner.name = Objects.requireNonNull(name);
        banner.status = PromotionBannerStatus.DRAFT;
        banner.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        banner.createdBy = createdBy;
        banner.createdAt = now;
        banner.updatedBy = createdBy;
        banner.updatedAt = now;
        return banner;
    }

    public PromotionBanner update(
        final String name,
        final List<PromotionBannerItem> items,
        final String updatedBy
    ) {
        this.name = Objects.requireNonNull(name);
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public PromotionBanner changeStatus(final PromotionBannerStatus status, final String updatedBy) {
        this.status = Objects.requireNonNull(status);
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        return this;
    }

    public List<PromotionBannerItem> sortedItems() {
        return items.stream()
            .sorted(Comparator.comparing(item -> item.getSortOrder() == null ? 0 : item.getSortOrder()))
            .toList();
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public PromotionBannerStatus getStatus() {
        return status;
    }

    public List<PromotionBannerItem> getItems() {
        return items;
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
