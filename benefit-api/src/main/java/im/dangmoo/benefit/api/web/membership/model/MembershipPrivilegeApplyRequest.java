package im.dangmoo.benefit.api.web.membership.model;

public record MembershipPrivilegeApplyRequest(
    long amount,
    String orderId
) {
}
