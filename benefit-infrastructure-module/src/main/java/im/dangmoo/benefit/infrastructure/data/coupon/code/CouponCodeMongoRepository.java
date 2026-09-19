package im.dangmoo.benefit.infrastructure.data.coupon.code;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponCodeMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponCodeMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<CouponCode> findByCode(final String code) {
        return Optional.ofNullable(mongoTemplate.findOne(CouponCode.queryByCode(code), CouponCode.class));
    }

    public boolean existsByCode(final String code) {
        return mongoTemplate.exists(CouponCode.queryByCode(code), CouponCode.class);
    }

    public long countByPolicyId(final String policyId) {
        return mongoTemplate.count(CouponCode.queryByPolicyId(policyId), CouponCode.class);
    }

    public List<CouponCode> findByPolicyId(final String policyId) {
        return mongoTemplate.find(CouponCode.queryByPolicyId(policyId), CouponCode.class);
    }

    public CouponCode insert(final CouponCode code) {
        return mongoTemplate.insert(code);
    }

    public CouponCode save(final CouponCode code) {
        return mongoTemplate.save(code);
    }

    public Optional<CouponCode> insertIgnoreDuplicate(final CouponCode code) {
        try {
            return Optional.of(mongoTemplate.insert(code));
        } catch (final DuplicateKeyException ignored) {
            return Optional.empty();
        }
    }

    public Optional<CouponCode> redeem(final String codeId, final String updatedBy) {
        return Optional.ofNullable(
            mongoTemplate.findAndModify(
                Query.query(
                    Criteria.where("_id").is(codeId)
                        .and(CouponCode.STATUS).is(CouponCodeStatus.AVAILABLE)
                ),
                new Update()
                    .set(CouponCode.STATUS, CouponCodeStatus.EXHAUSTED)
                    .set("updatedBy", updatedBy)
                    .currentDate("updatedAt"),
                FindAndModifyOptions.options().returnNew(true),
                CouponCode.class
            )
        );
    }
}
