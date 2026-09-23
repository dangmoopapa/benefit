package im.dangmoo.benefit.infrastructure.data.promotion.winner;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PromotionWinnerMongoRepository {

    private final MongoTemplate mongoTemplate;

    public PromotionWinnerMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PromotionWinnerDocument save(final PromotionWinnerDocument winner) {
        return mongoTemplate.save(winner);
    }

    public Optional<PromotionWinnerDocument> findByPolicyIdAndUserId(final String policyId, final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                PromotionWinnerDocument.queryByPolicyIdAndUserId(policyId, userId),
                PromotionWinnerDocument.class
            )
        );
    }

    public List<PromotionWinnerDocument> findByPolicyId(final String policyId) {
        return mongoTemplate.find(
            PromotionWinnerDocument.queryByPolicyId(policyId),
            PromotionWinnerDocument.class
        );
    }

    public boolean existsByPolicyId(final String policyId) {
        return mongoTemplate.exists(
            PromotionWinnerDocument.queryByPolicyId(policyId),
            PromotionWinnerDocument.class
        );
    }
}
