package im.dangmoo.benefit.domain.infrastructure.mongo;

import java.util.List;

public record DocumentSlice<T>(
    List<T> content,
    int size,
    boolean hasNext,
    String nextCursor
) {
}
