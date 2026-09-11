package im.dangmoo.benefit.domain.data.membership.subscription;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MembershipSubscriptionRepository {

    private final MongoTemplate mongoTemplate;

    public MembershipSubscriptionRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<MembershipSubscription> findByUserId(final String userId) {
        return Optional.ofNullable(
            mongoTemplate.findOne(MembershipSubscription.queryByUserId(userId), MembershipSubscription.class)
        );
    }

    public MembershipSubscription save(final MembershipSubscription subscription) {
        return mongoTemplate.save(subscription);
    }
}
