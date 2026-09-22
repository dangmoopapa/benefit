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

    public PromotionWinner save(final PromotionWinner winner) {
        return mongoTemplate.save(winner);
    }

    public Optional<PromotionWinner> findByPolicyIdAndUserId(final String policyId, final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(
                PromotionWinner.queryByPolicyIdAndUserId(policyId, userId),
                PromotionWinner.class
            )
        );
    }

    public List<PromotionWinner> findByPolicyId(final String policyId) {
        return mongoTemplate.find(
            PromotionWinner.queryByPolicyId(policyId),
            PromotionWinner.class
        );
    }

    public boolean existsByPolicyId(final String policyId) {
        return mongoTemplate.exists(
            PromotionWinner.queryByPolicyId(policyId),
            PromotionWinner.class
        );
    }
}
