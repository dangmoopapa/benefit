package im.dangmoo.benefit.admin.model.membership.history;

public record MembershipBenefitHistorySearchRequest(
    String userId,
    String contractId,
    Integer page,
    Integer size
) {
    public int pageOrDefault() {
        return page == null || page < 0 ? 0 : page;
    }

    public int sizeOrDefault() {
        return size == null || size <= 0 ? 20 : Math.min(size, 100);
    }
}
