package im.dangmoo.benefit.domain.data.coupon.policy.usage;

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
        final CouponUsageLimit entity = new CouponUsageLimit();
        entity.perUser = perUser;
        entity.perUserPerMonth = perUserPerMonth;
        entity.perOrder = perOrder;
        entity.total = total;
        entity.perDay = perDay;
        entity.perHour = perHour;
        return entity;
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
