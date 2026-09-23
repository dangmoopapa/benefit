package im.dangmoo.benefit.api.dto.membership;

public record MembershipBenefitHistoryPageRequest(Integer page, Integer size) {
    public int pageOrDefault() {
        return page == null || page < 0 ? 0 : page;
    }

    public int sizeOrDefault() {
        return size == null || size <= 0 ? 20 : Math.min(size, 100);
    }
}
