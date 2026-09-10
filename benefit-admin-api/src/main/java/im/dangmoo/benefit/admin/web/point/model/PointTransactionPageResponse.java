package im.dangmoo.benefit.admin.web.point.model;

import im.dangmoo.benefit.domain.infrastructure.mongo.DocumentSlice;

import java.util.List;

public record PointTransactionPageResponse(
    List<PointTransactionResponse> content,
    int size,
    boolean hasNext,
    String nextCursor
) {

    public static PointTransactionPageResponse of(
        final DocumentSlice<?> slice,
        final List<PointTransactionResponse> content
    ) {
        return new PointTransactionPageResponse(
            content,
            slice.size(),
            slice.hasNext(),
            slice.nextCursor()
        );
    }
}
