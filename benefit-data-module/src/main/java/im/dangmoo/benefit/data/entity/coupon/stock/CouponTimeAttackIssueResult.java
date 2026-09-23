package im.dangmoo.benefit.data.entity.coupon.stock;

import org.springframework.util.StringUtils;

public record CouponTimeAttackIssueResult(
    Status status,
    long issuedCount
) {

    public enum Status {
        RESERVED,
        ALREADY_ISSUED,
        SOLD_OUT
    }

    private static final String REPLY_DELIMITER = ":";
    private static final String REPLY_OK = "OK";
    private static final String REPLY_ALREADY_ISSUED = "ALREADY_ISSUED";
    private static final String REPLY_SOLD_OUT = "SOLD_OUT";

    public static CouponTimeAttackIssueResult fromReply(final String reply) {
        final String[] parts = StringUtils.delimitedListToStringArray(reply, REPLY_DELIMITER);
        final Status status = switch (parts[0]) {
            case REPLY_OK -> Status.RESERVED;
            case REPLY_ALREADY_ISSUED -> Status.ALREADY_ISSUED;
            case REPLY_SOLD_OUT -> Status.SOLD_OUT;
            default -> throw new IllegalStateException("Unknown time-attack issue reply: " + parts[0]);
        };
        final long issuedCount = parts.length > 1 ? Long.parseLong(parts[1]) : 0L;
        return new CouponTimeAttackIssueResult(status, issuedCount);
    }

    public boolean isAlreadyIssued() {
        return status == Status.ALREADY_ISSUED;
    }

    public boolean isSoldOut() {
        return status == Status.SOLD_OUT;
    }
}
