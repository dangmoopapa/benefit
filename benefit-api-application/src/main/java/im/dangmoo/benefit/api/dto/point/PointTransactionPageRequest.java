package im.dangmoo.benefit.api.dto.point;

public record PointTransactionPageRequest(
    Integer page,
    Integer size
) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public int pageOrDefault() {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    public int sizeOrDefault() {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
