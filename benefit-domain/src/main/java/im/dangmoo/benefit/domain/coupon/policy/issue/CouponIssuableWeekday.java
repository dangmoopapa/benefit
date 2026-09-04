package im.dangmoo.benefit.domain.coupon.policy.issue;

import java.time.DayOfWeek;

public enum CouponIssuableWeekday {
    MON(DayOfWeek.MONDAY),
    TUE(DayOfWeek.TUESDAY),
    WED(DayOfWeek.WEDNESDAY),
    THU(DayOfWeek.THURSDAY),
    FRI(DayOfWeek.FRIDAY),
    SAT(DayOfWeek.SATURDAY),
    SUN(DayOfWeek.SUNDAY);

    public final DayOfWeek dayOfWeek;

    CouponIssuableWeekday(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
