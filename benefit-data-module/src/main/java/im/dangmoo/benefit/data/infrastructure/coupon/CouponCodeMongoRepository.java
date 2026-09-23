package im.dangmoo.benefit.data.infrastructure.coupon;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import im.dangmoo.benefit.data.entity.coupon.code.*;

@Repository
public class CouponCodeMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CouponCodeMongoRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<CouponCodeDocument> findByCode(final String code) {
        return Optional.ofNullable(mongoTemplate.findOne(CouponCodeDocument.queryByCode(code), CouponCodeDocument.class));
    }

    public List<CouponCodeDocument> findByPolicyId(final String policyId) {
        return mongoTemplate.find(CouponCodeDocument.queryByPolicyId(policyId), CouponCodeDocument.class);
    }

    public boolean existsByCode(final String code) {
        return mongoTemplate.exists(CouponCodeDocument.queryByCode(code), CouponCodeDocument.class);
    }

    public long countByPolicyId(final String policyId) {
        return mongoTemplate.count(CouponCodeDocument.queryByPolicyId(policyId), CouponCodeDocument.class);
    }

    public void insert(final CouponCodeDocument code) {
        mongoTemplate.insert(code);
    }

    public CouponCodeDocument save(final CouponCodeDocument code) {
        return mongoTemplate.save(code);
    }

    public Optional<CouponCodeDocument> insertIgnoreDuplicate(final CouponCodeDocument code) {
        try {
            return Optional.of(mongoTemplate.insert(code));
        } catch (final DuplicateKeyException ignored) {
            return Optional.empty();
        }
    }

    public Optional<CouponCodeDocument> redeem(final String codeId, final String updatedBy) {
        return Optional.ofNullable(
            mongoTemplate.findAndModify(
                Query.query(
                    Criteria.where("_id").is(codeId)
                        .and(CouponCodeDocument.STATUS).is(CouponCodeStatus.AVAILABLE)
                ),
                new Update()
                    .set(CouponCodeDocument.STATUS, CouponCodeStatus.EXHAUSTED)
                    .set("updatedBy", updatedBy)
                    .currentDate("updatedAt"),
                FindAndModifyOptions.options().returnNew(true),
                CouponCodeDocument.class
            )
        );
    }
}
