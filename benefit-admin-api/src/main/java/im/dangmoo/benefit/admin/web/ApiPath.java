package im.dangmoo.benefit.admin.web;

public final class ApiPath {

    public static final String COUPON_POLICIES = "/coupons/policies";
    public static final String COUPON_POLICY = "/coupons/policies/{policyId}";
    public static final String COUPON_POLICY_ACTIVATE = "/coupons/policies/{policyId}/activate";
    public static final String COUPON_POLICY_SUSPEND = "/coupons/policies/{policyId}/suspend";
    public static final String COUPON_WALLETS_BY_POLICY = "/coupons/wallets/by-policy";
    public static final String COUPON_WALLETS_BY_USER = "/coupons/wallets/by-user";
    public static final String COUPON_WALLET_ISSUE = "/coupons/wallets/issue";
    public static final String COUPON_WALLET_USE = "/coupons/wallets/{walletId}/use";
    public static final String COUPON_WALLET_RECOVER = "/coupons/wallets/{walletId}/recover";
}
