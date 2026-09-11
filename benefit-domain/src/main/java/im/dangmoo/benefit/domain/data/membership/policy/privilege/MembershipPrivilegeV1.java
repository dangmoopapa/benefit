package im.dangmoo.benefit.domain.data.membership.policy.privilege;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("V1")
public final class MembershipPrivilegeV1 implements MembershipPrivilege {

    public static final int VERSION = 1;
    public static final String POINT_POLICY_CODE = "MEMBERSHIP_V1";
    public static final String COUPON_POLICY_CODE = "MEMBERSHIP_V1";

    public MembershipPrivilegeV1() {
    }

    public long cashbackPoint(final long amount) {
        if (amount <= 0) {
            return 0L;
        }
        return amount / 10L;
    }
}
