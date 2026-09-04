package im.dangmoo.benefit.domain.coupon.policy.usage;

public class CouponUsageLimit {

    private Integer perUser;
    private Integer perUserPerMonth;
    private Integer perOrder;
    private Long total;
    private Integer perDay;
    private Integer perHour;

    protected CouponUsageLimit() {
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
}
