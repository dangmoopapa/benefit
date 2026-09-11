package im.dangmoo.benefit.domain.data.membership.policy.privilege;

public sealed interface MembershipPrivilege permits MembershipPrivilegeV1 {

    static MembershipPrivilege of(final int version) {
        if (version == MembershipPrivilegeV1.VERSION) {
            return new MembershipPrivilegeV1();
        }
        return null;
    }
}
