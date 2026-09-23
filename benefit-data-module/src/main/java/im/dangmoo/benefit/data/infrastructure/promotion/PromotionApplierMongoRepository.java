package im.dangmoo.benefit.data.infrastructure.promotion;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import im.dangmoo.benefit.data.entity.promotion.applier.*;

@Repository
public class PromotionApplierMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionApplierMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionApplierDocument save(final PromotionApplierDocument applier) {
        return mongoTemplate.save(applier);
    }

    public boolean existsByPolicyIdAndUserId(final String policyId, final String userId) {
        return mongoTemplate.exists(
            PromotionApplierDocument.queryByPolicyIdAndUserId(policyId, userId),
            PromotionApplierDocument.class
        );
    }

    public List<PromotionApplierDocument> findByPolicyId(final String policyId) {
        return mongoTemplate.find(
            PromotionApplierDocument.queryByPolicyId(policyId),
            PromotionApplierDocument.class
        );
    }
}
