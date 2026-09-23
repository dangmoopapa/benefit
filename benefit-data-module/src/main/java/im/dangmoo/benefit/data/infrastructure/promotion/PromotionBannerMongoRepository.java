package im.dangmoo.benefit.data.infrastructure.promotion;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.promotion.banner.*;

@Repository
public class PromotionBannerMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionBannerMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionBannerDocument save(final PromotionBannerDocument banner) {
        return mongoTemplate.save(banner);
    }

    public Optional<PromotionBannerDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PromotionBannerDocument.class));
    }

    public Optional<PromotionBannerDocument> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PromotionBannerDocument.queryByKey(key), PromotionBannerDocument.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PromotionBannerDocument.queryByKey(key), PromotionBannerDocument.class);
    }

    public List<PromotionBannerDocument> search(
        final String key,
        final String name,
        final PromotionBannerStatus status
    ) {
        return mongoTemplate.find(
            PromotionBannerDocument.query(key, name, status),
            PromotionBannerDocument.class
        );
    }
}
