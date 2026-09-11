package im.dangmoo.benefit.domain.data.membership.policy.privilege;

import im.dangmoo.benefit.domain.data.membership.policy.MembershipPeriod;
import im.dangmoo.benefit.domain.data.membership.policy.MembershipPolicy;

public class CachedMembershipPrivilege {

    private String policyId;
    private int version;
    private MembershipPrivilege privilege;
    private MembershipPeriod period;

    private CachedMembershipPrivilege() {
    }

    public static CachedMembershipPrivilege of(final MembershipPolicy policy) {
        final CachedMembershipPrivilege cached = new CachedMembershipPrivilege();
        cached.policyId = policy.getId();
        cached.version = policy.getVersion();
        cached.privilege = policy.getPrivilege();
        cached.period = policy.getPeriod();
        return cached;
    }

    public String getPolicyId() {
        return policyId;
    }

    public int getVersion() {
        return version;
    }

    public MembershipPrivilege getPrivilege() {
        return privilege;
    }

    public MembershipPeriod getPeriod() {
        return period;
    }
}
