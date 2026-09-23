package im.dangmoo.benefit.infrastructure.data.promotion.winner;

import im.dangmoo.benefit.infrastructure.support.mongo.MongoDocuments;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionLotteryType;
import im.dangmoo.benefit.infrastructure.data.promotion.feature.PromotionPrize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = MongoDocuments.PROMOTION_WINNERS)
@CompoundIndex(name = "uk_policyId_userId", def = "{'policyId': 1, 'userId': 1}", unique = true)
public class PromotionWinnerDocument {

    @Id
    private String id;
    private String policyId;
    private String policyKey;
    private String userId;
    private PromotionLotteryType lotteryType;
    private List<PromotionPrize> prizes = new ArrayList<>();
    private String drawnBy;
    private Instant drawnAt;

    public static final String POLICY_ID = "policyId";
    public static final String USER_ID = "userId";

    private PromotionWinnerDocument() {
    }

    public static Query queryByPolicyId(final String policyId) {
        return Query.query(Criteria.where(POLICY_ID).is(policyId));
    }

    public static Query queryByPolicyIdAndUserId(final String policyId, final String userId) {
        return Query.query(
            Criteria.where(POLICY_ID).is(policyId).and(USER_ID).is(userId)
        );
    }

    public static PromotionWinnerDocument draw(
        final String policyId,
        final String policyKey,
        final String userId,
        final PromotionLotteryType lotteryType,
        final List<PromotionPrize> prizes,
        final String drawnBy
    ) {
        final PromotionWinnerDocument winner = new PromotionWinnerDocument();
        winner.policyId = Objects.requireNonNull(policyId);
        winner.policyKey = Objects.requireNonNull(policyKey);
        winner.userId = Objects.requireNonNull(userId);
        winner.lotteryType = Objects.requireNonNull(lotteryType);
        winner.prizes = prizes == null ? new ArrayList<>() : new ArrayList<>(prizes);
        winner.drawnBy = drawnBy;
        winner.drawnAt = Instant.now();
        return winner;
    }

    public String getId() {
        return id;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public String getUserId() {
        return userId;
    }

    public PromotionLotteryType getLotteryType() {
        return lotteryType;
    }

    public List<PromotionPrize> getPrizes() {
        return prizes;
    }

    public String getDrawnBy() {
        return drawnBy;
    }

    public Instant getDrawnAt() {
        return drawnAt;
    }
}
