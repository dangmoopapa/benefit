package im.dangmoo.benefit.domain.coupon.data.stock;

import org.springframework.util.StringUtils;

/**
 * reserve 결과. 재고 확보 성공/실패 결과만 담는다.
 */
public enum CouponStockReserveResult {

    RESERVED,
    ALREADY_ISSUED,
    SOLD_OUT;

    private static final String REPLY_DELIMITER = ":";
    private static final String REPLY_OK = "OK";
    private static final String REPLY_ALREADY_ISSUED = "ALREADY_ISSUED";
    private static final String REPLY_SOLD_OUT = "SOLD_OUT";

    static CouponStockReserveResult fromReply(final String reply) {
        final String code = StringUtils.delimitedListToStringArray(reply, REPLY_DELIMITER)[0];
        return switch (code) {
            case REPLY_OK -> RESERVED;
            case REPLY_ALREADY_ISSUED -> ALREADY_ISSUED;
            case REPLY_SOLD_OUT -> SOLD_OUT;
            default -> throw new IllegalStateException("Unknown stock reserve reply: " + code);
        };
    }
}
