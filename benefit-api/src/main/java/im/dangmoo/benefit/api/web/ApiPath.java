package im.dangmoo.benefit.api.web;

public final class ApiPath {

    public static final String COUPON_BOX = "/coupons/box";
    public static final String COUPON_ISSUE = "/coupons/issue";
    public static final String COUPON_ISSUE_CHECK = "/coupons/issue/check";
    public static final String COUPON_USE = "/coupons/wallets/{walletId}/use";
    public static final String COUPON_CANCEL = "/coupons/wallets/{walletId}/cancel";

    public static final String POINT_BOOK_BALANCES = "/points/book/balances";
    public static final String POINT_BOOK_TRANSACTIONS = "/points/book/transactions";
    public static final String POINT_ISSUE = "/points/issue";
    public static final String POINT_REVOKE = "/points/revoke";
    public static final String POINT_USE = "/points/use";
    public static final String POINT_RESTORE = "/points/restore";
}
