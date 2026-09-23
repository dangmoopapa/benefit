package im.dangmoo.benefit.api.dto.point;

import im.dangmoo.benefit.api.usecase.ApiMessage;

public record PointIssuableResponse(
    boolean issuable,
    String reason
) {

    public static PointIssuableResponse ofIssuable() {
        return new PointIssuableResponse(true, null);
    }

    public static PointIssuableResponse ofNotIssuable(final ApiMessage reason) {
        return new PointIssuableResponse(false, reason.name());
    }
}
