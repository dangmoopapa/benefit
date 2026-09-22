package im.dangmoo.benefit.infrastructure.data.promotion.banner;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PromotionBannerMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionBannerMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionBanner save(final PromotionBanner banner) {
        return mongoTemplate.save(banner);
    }

    public Optional<PromotionBanner> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PromotionBanner.class));
    }

    public Optional<PromotionBanner> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PromotionBanner.queryByKey(key), PromotionBanner.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PromotionBanner.queryByKey(key), PromotionBanner.class);
    }

    public List<PromotionBanner> search(
        final String key,
        final String name,
        final PromotionBannerStatus status
    ) {
        return mongoTemplate.find(
            PromotionBanner.query(key, name, status),
            PromotionBanner.class
        );
    }
}
