package im.dangmoo.benefit.infrastructure.data.promotion.policy;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PromotionPolicyMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionPolicyMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionPolicy save(final PromotionPolicy policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<PromotionPolicy> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PromotionPolicy.class));
    }

    public Optional<PromotionPolicy> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PromotionPolicy.queryByKey(key), PromotionPolicy.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PromotionPolicy.queryByKey(key), PromotionPolicy.class);
    }

    public List<PromotionPolicy> search(
        final String key,
        final String title,
        final im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus status
    ) {
        return mongoTemplate.find(
            PromotionPolicy.query(key, title, status),
            PromotionPolicy.class
        );
    }

    public List<PromotionPolicy> findActiveList(final Instant now) {
        return mongoTemplate.find(
            PromotionPolicy.queryActiveList(now).with(Sort.by(Sort.Direction.ASC, PromotionPolicy.SORT_ORDER)),
            PromotionPolicy.class
        );
    }

    public List<PromotionPolicy> findEndedForAutoLottery(final Instant now) {
        return mongoTemplate.find(
            PromotionPolicy.queryEndedForAutoLottery(now),
            PromotionPolicy.class
        );
    }
}
