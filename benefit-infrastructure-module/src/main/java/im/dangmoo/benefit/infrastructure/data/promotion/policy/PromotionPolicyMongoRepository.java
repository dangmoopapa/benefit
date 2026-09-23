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

    public PromotionPolicyDocument save(final PromotionPolicyDocument policy) {
        return mongoTemplate.save(policy);
    }

    public Optional<PromotionPolicyDocument> findById(final String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, PromotionPolicyDocument.class));
    }

    public Optional<PromotionPolicyDocument> findByKey(final String key) {
        return Optional.ofNullable(
            mongoTemplate.findOne(PromotionPolicyDocument.queryByKey(key), PromotionPolicyDocument.class)
        );
    }

    public boolean existsByKey(final String key) {
        return mongoTemplate.exists(PromotionPolicyDocument.queryByKey(key), PromotionPolicyDocument.class);
    }

    public List<PromotionPolicyDocument> search(
        final String key,
        final String title,
        final im.dangmoo.benefit.infrastructure.data.promotion.PromotionPolicyStatus status
    ) {
        return mongoTemplate.find(
            PromotionPolicyDocument.query(key, title, status),
            PromotionPolicyDocument.class
        );
    }

    public List<PromotionPolicyDocument> findActiveList(final Instant now) {
        return mongoTemplate.find(
            PromotionPolicyDocument.queryActiveList(now).with(Sort.by(Sort.Direction.ASC, PromotionPolicyDocument.SORT_ORDER)),
            PromotionPolicyDocument.class
        );
    }

    public List<PromotionPolicyDocument> findEndedForAutoLottery(final Instant now) {
        return mongoTemplate.find(
            PromotionPolicyDocument.queryEndedForAutoLottery(now),
            PromotionPolicyDocument.class
        );
    }
}
