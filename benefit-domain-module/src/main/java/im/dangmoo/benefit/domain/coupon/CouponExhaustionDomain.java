package im.dangmoo.benefit.domain.coupon;

import im.dangmoo.benefit.infrastructure.data.coupon.policy.condition.CouponIssueCondition;

public class CouponExhaustionDomain {

    private final Long stockQuantity;

    private CouponExhaustionDomain(final Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public static CouponExhaustionDomain of(final CouponIssueCondition condition) {
        return new CouponExhaustionDomain(condition.getStockQuantity());
    }

    public boolean isExhausted(final long issuedCount) {
        return stockQuantity != null && issuedCount >= stockQuantity;
    }

    public boolean isJustExhausted(final long issuedCountAfterIssue) {
        return stockQuantity != null && issuedCountAfterIssue == stockQuantity;
    }
}
