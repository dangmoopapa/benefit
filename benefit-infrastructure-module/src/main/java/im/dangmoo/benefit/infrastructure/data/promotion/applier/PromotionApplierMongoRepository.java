package im.dangmoo.benefit.infrastructure.data.promotion.applier;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PromotionApplierMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionApplierMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionApplier save(final PromotionApplier applier) {
        return mongoTemplate.save(applier);
    }

    public boolean existsByPolicyIdAndUserId(final String policyId, final String userId) {
        return mongoTemplate.exists(
            PromotionApplier.queryByPolicyIdAndUserId(policyId, userId),
            PromotionApplier.class
        );
    }

    public List<PromotionApplier> findByPolicyId(final String policyId) {
        return mongoTemplate.find(
            PromotionApplier.queryByPolicyId(policyId),
            PromotionApplier.class
        );
    }
}
