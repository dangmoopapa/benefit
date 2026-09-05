package im.dangmoo.benefit.api.support;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public final class UserHeaders {

    public static final String USER_ID = "X-User-Id";
    public static final String SEGMENT_IDS = "X-Segment-Ids";

    private UserHeaders() {
    }

    public static List<String> parseSegmentIds(final String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return List.of();
        }
        return Arrays.stream(headerValue.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .toList();
    }
}
