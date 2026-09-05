package im.dangmoo.benefit.domain.data.coupon.policy.usage;

import java.time.DayOfWeek;

public enum CouponUsableWeekday {
    MON(DayOfWeek.MONDAY),
    TUE(DayOfWeek.TUESDAY),
    WED(DayOfWeek.WEDNESDAY),
    THU(DayOfWeek.THURSDAY),
    FRI(DayOfWeek.FRIDAY),
    SAT(DayOfWeek.SATURDAY),
    SUN(DayOfWeek.SUNDAY);

    public final DayOfWeek dayOfWeek;

    CouponUsableWeekday(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
