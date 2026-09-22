package im.dangmoo.benefit.api.controller;

public final class ApiPath {

    public static final String COUPON_BOX = "/coupon/box";
    public static final String COUPON_ISSUE = "/coupon/issue";
    public static final String COUPON_ISSUABLE = "/coupon/issuable";
    public static final String COUPON_USABLE = "/coupon/usable";
    public static final String COUPON_USAGE = "/coupon/usage";
    public static final String COUPON_RECOVERY = "/coupon/recovery";
    public static final String COUPON_VOUCHER_BOX = "/coupon/vouchers/box";
    public static final String COUPON_TIME_ATTACK_ISSUE = "/coupon/time-attacks/issue";
    public static final String COUPON_TIME_ATTACK_ISSUABLE = "/coupon/time-attacks/issuable";
    public static final String COUPON_MARKETING_ISSUE = "/coupon/marketings/issue";

    public static final String POINT_ISSUABLE = "/point/issuable";
    public static final String POINT_CLAIM = "/point/claim";
    public static final String POINT_RECLAIM = "/point/reclaim";
    public static final String POINT_USAGE = "/point/usage";
    public static final String POINT_RECOVERY = "/point/recovery";
    public static final String POINT_BALANCES = "/point/balances";
    public static final String POINT_TRANSACTIONS = "/point/transactions";

    public static final String MEMBERSHIP_CONTRACTS = "/membership/contracts";
    public static final String MEMBERSHIP_CONTRACT_JOIN = "/membership/contracts/join";
    public static final String MEMBERSHIP_CONTRACT_LEAVE = "/membership/contracts/leave";
    public static final String MEMBERSHIP_BENEFIT_APPLY = "/membership/benefit/apply";
    public static final String MEMBERSHIP_BENEFIT_CANCEL = "/membership/benefit/cancel";
    public static final String MEMBERSHIP_BENEFIT_HISTORIES = "/membership/benefit/histories";
    public static final String MEMBERSHIP_BENEFIT_COUPON_ISSUE = "/membership/benefit/coupon/issue";

    public static final String PROMOTIONS = "/promotions";
    public static final String PROMOTION = "/promotions/{key}";
    public static final String PROMOTION_APPLY = "/promotions/{key}/apply";
    public static final String PROMOTION_BANNERS = "/promotion/banners/{key}";

    private ApiPath() {
    }
}
