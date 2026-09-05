package im.dangmoo.benefit.domain.coupon.document.policy.usage;

public class CouponUsageLimit {

    private Integer perUser;
    private Integer perUserPerMonth;
    private Integer perOrder;
    private Long total;
    private Integer perDay;
    private Integer perHour;

    private CouponUsageLimit() {
    }

    public static CouponUsageLimit create(
        final Integer perUser,
        final Integer perUserPerMonth,
        final Integer perOrder,
        final Long total,
        final Integer perDay,
        final Integer perHour
    ) {
        final CouponUsageLimit document = new CouponUsageLimit();
        document.perUser = perUser;
        document.perUserPerMonth = perUserPerMonth;
        document.perOrder = perOrder;
        document.total = total;
        document.perDay = perDay;
        document.perHour = perHour;
        return document;
    }

    public Integer getPerUser() {
        return perUser;
    }

    public Integer getPerUserPerMonth() {
        return perUserPerMonth;
    }

    public Integer getPerOrder() {
        return perOrder;
    }

    public Long getTotal() {
        return total;
    }

    public Integer getPerDay() {
        return perDay;
    }

    public Integer getPerHour() {
        return perHour;
    }

    public boolean isSatisfiedBy(final CouponUsageCountSnapshot counts) {
        if (perUser != null && counts.perUser() >= perUser) {
            return false;
        }
        if (perUserPerMonth != null && counts.perUserPerMonth() >= perUserPerMonth) {
            return false;
        }
        if (perOrder != null && counts.perOrder() >= perOrder) {
            return false;
        }
        if (total != null && counts.total() >= total) {
            return false;
        }
        if (perDay != null && counts.perDay() >= perDay) {
            return false;
        }
        return perHour == null || counts.perHour() < perHour;
    }
}
